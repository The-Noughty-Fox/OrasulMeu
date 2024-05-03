import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Repository } from 'typeorm';
import { SocialMedia } from '@/shared/types';
import { Mapper } from '@automapper/core';
import { InjectMapper } from '@automapper/nestjs';
import { UserDto } from './dto/user.dto';
import { UserCreateDto } from './dto/user-create.dto';
import { UserProfileDto } from '@/resources/user/dto/user-profile.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private readonly userRepository: Repository<User>,
    @InjectMapper()
    private readonly mapper: Mapper,
  ) {}

  async create(createUserDto: UserCreateDto) {
    const userEntity = this.userRepository.create(createUserDto);

    const user = await this.userRepository.save(userEntity);

    return this.mapper.map(user, User, UserDto);
  }

  async findAll() {
    const users = await this.userRepository.find();

    return this.mapper.mapArray(users, User, UserDto);
  }

  async findOne(id: number) {
    const user = await this.userRepository.findOne({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException(`User with id ${id} not found`);
    }

    return this.mapper.map(user, User, UserDto);
  }

  async update(id: number, updateUserDto: UserDto) {
    const userEntity = await this.userRepository.findOne({
      where: { id },
    });

    if (!userEntity) {
      throw new NotFoundException(`User with id ${id} not found`);
    }

    Object.assign(userEntity, updateUserDto);

    const user = await this.userRepository.save(userEntity);

    return this.mapper.map(user, User, UserDto);
  }

  async findBySocialMediaToken(socialMedia: SocialMedia, token: string) {
    try {
      let where = {};

      switch (socialMedia) {
        case SocialMedia.Apple: {
          where = { apple_token: token };
          break;
        }
        case SocialMedia.Facebook: {
          where = { facebook_token: token };
          break;
        }
        case SocialMedia.Google: {
          where = { google_token: token };
          break;
        }
      }

      const user = await this.userRepository.findOne({
        where,
      });

      return this.mapper.map(user, User, UserDto);
    } catch (error) {}
  }

  async findByEmail(email: string) {
    const user = await this.userRepository.findOne({
      where: { email },
    });

    return this.mapper.map(user, User, UserDto);
  }

  async profile(userId: number): Promise<UserProfileDto> {
    const result = await this.userRepository
      .createQueryBuilder('users')
      .leftJoinAndSelect('users.posts', 'posts')
      .leftJoinAndSelect('users.postReactions', 'postReactions')
      .select([
        'users.id',
        'users.email',
        'users.firstName',
        'users.lastName',
        'users.socialProfilePictureUrl',
        'COUNT(DISTINCT posts.id) AS posts_count',
        'COUNT(DISTINCT postReactions.id) AS posts_reactions_count',
      ])
      .where('users.id = :userId', { userId })
      .groupBy('users.id')
      .getRawOne();

    return {
      id: result.users_id,
      email: result.users_email,
      firstName: result.users_firstName,
      lastName: result.users_lastName,
      socialProfilePictureUrl: result.users_socialProfilePictureUrl,
      publicationsCount: parseInt(result.posts_count),
      reactionsCount: parseInt(result.posts_reactions_count),
    };
  }
}
