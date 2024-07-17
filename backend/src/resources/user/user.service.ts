import {
  BadRequestException,
  ConflictException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Repository } from 'typeorm';
import { SocialMedia } from '@/shared/types';
import { Mapper } from '@automapper/core';
import { InjectMapper } from '@automapper/nestjs';
import { UserDto } from './dto/user.dto';
import { UserCreateDto } from './dto/user-create.dto';
import { UserProfileDto } from '@/resources/user/dto/user-profile.dto';
import { SupabaseService } from '../supabase/supabase.service';
import { UserUpdateDto } from './dto/user-update.dto';
import { SupabaseClient } from '@supabase/supabase-js';

@Injectable()
export class UserService {
  private readonly supabase: SupabaseClient;

  constructor(
    @InjectRepository(User) private readonly userRepository: Repository<User>,
    @InjectMapper()
    private readonly mapper: Mapper,
    private readonly supabaseService: SupabaseService,
  ) {
    this.supabase = this.supabaseService.getClient();
  }

  async create(createUserDto: UserCreateDto): Promise<UserDto> {
    if (
      !createUserDto.appleToken &&
      !createUserDto.googleToken &&
      !createUserDto.facebookToken
    ) {
      throw new BadRequestException('Provide a token to create a user');
    }

    const { data: user, error } = await this.supabase
      .from('custom_users')
      .insert([createUserDto])
      .select('id, email, firstName, lastName, socialProfilePictureUrl');

    // one possible reason for fail of creating user is that the email is already taken
    // email is unique in the database, same as each type of token, no two same google tokens
    // but still could be other type of error
    if (error || !user || user.length === 0) {
      if (error.code === '23505') {
        throw new ConflictException('Invalid input data');
      } else {
        throw new InternalServerErrorException('Could not create the user');
      }
    }

    return user[0] as UserDto;
  }

  async findAll(): Promise<UserDto[]> {
    const { data: users, error } = await this.supabase
      .from('custom_users')
      .select('id, email, firstName, lastName, socialProfilePictureUrl');

    if (error) {
      throw new InternalServerErrorException('Could not find the users');
    }

    if (users.length === 0) {
      throw new NotFoundException(`No users found`);
    }

    return users as UserDto[];
  }

  async findOne(id: number): Promise<UserDto> {
    const { data: user, error } = await this.supabase
      .from('custom_users')
      .select('id, email, firstName, lastName, socialProfilePictureUrl')
      .eq('id', id);

    if (error) {
      throw new InternalServerErrorException('Could not find the user');
    }

    if (!user || user.length === 0) {
      throw new NotFoundException(`User with id ${id} not found`);
    }

    return user[0] as UserDto;
  }

  async update(id: number, updateUserDto: UserUpdateDto): Promise<UserDto> {
    const existingUser = await this.findOne(id);

    const user = { ...existingUser, ...updateUserDto };

    const { data: updatedUser, error: updatedError } = await this.supabase
      .from('custom_users')
      .update(user)
      .eq('id', id)
      .select('id, email, firstName, lastName, socialProfilePictureUrl');

    if (!updatedUser || updatedUser.length === 0 || updatedError) {
      throw new InternalServerErrorException('Could not update the user');
    }

    return updatedUser[0] as UserDto;
  }

  async findBySocialMediaToken(
    socialMedia: SocialMedia,
    token: string,
  ): Promise<UserDto> {
    const { data: user, error } = await this.supabase
      .from('custom_users')
      .select('id, email, firstName, lastName, socialProfilePictureUrl')
      .eq(`${socialMedia}Token`, token);

    if (error) {
      throw new InternalServerErrorException('Could not find the user');
    }

    if (!user || user.length === 0) {
      throw new NotFoundException(`User with given token not found`);
    }

    return user[0] as UserDto;
  }

  async findByEmail(email: string): Promise<UserDto> {
    const { data: user, error } = await this.supabase
      .from('custom_users')
      .select('id, email, firstName, lastName, socialProfilePictureUrl')
      .eq('email', email);

    if (error) {
      throw new InternalServerErrorException('Could not find the user');
    }

    if (!user || user.length === 0) {
      throw new NotFoundException(`User with given email not found`);
    }

    return user[0] as UserDto;
  }

  async profile(id: number): Promise<UserProfileDto> {
    const { data: user, error } = await this.supabase.rpc('get_user_profile', {
      user_id_input: id,
    });

    if (error) {
      throw new InternalServerErrorException('Could not find users profile');
    }

    if (user.length === 0) {
      throw new NotFoundException(`User profile with id ${id} not found`);
    }

    return {
      id: user[0].id,
      email: user[0].email,
      firstName: user[0].firstName,
      lastName: user[0].lastName,
      socialProfilePictureUrl: user[0].socialProfilePictureUrl,
      publicationsCount: parseInt(user[0].postsCount),
      reactionsCount: parseInt(user[0].postsReactionsCount),
    };
  }
}
