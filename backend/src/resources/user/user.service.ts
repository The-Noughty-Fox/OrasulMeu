import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Repository } from 'typeorm';
import { SocialMedia } from '../../shared/types';
import { Mapper } from '@automapper/core';
import { InjectMapper } from '@automapper/nestjs';
import { UserDto } from './dto/user.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private readonly userRepository: Repository<User>,
    @InjectMapper()
    private readonly mapper: Mapper,
  ) {}

  async create(createUserDto: UserDto) {
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
}
