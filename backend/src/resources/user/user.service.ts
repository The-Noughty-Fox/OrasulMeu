import {
  BadRequestException,
  ConflictException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { SocialMedia } from '@/shared/types';
import { UserDto } from './dto/user.dto';
import { UserCreateDto } from './dto/user-create.dto';
import { UserProfileDto } from '@/resources/user/dto/user-profile.dto';
import { SupabaseService } from '../supabase/supabase.service';
import { UserUpdateDto } from './dto/user-update.dto';

@Injectable()
export class UserService {
  constructor(private readonly supabaseService: SupabaseService) {}

  async create(createUserDto: UserCreateDto): Promise<UserDto> {
    if (
      !createUserDto.appleToken &&
      !createUserDto.googleToken &&
      !createUserDto.facebookToken
    ) {
      throw new BadRequestException('Provide a token to create a user');
    }

    const { data: user, error } = await this.supabaseService
      .getClient()
      .from('custom_users')
      .insert([createUserDto])
      .select('id, email, username, socialProfilePictureUrl')
      .single();

    // possible errors:
    // 1. email already in use
    // 2. google | apple | facebook token already in use
    // 3. supabase failed to retrieve user from db
    // 4. supabase failed to insert user into db
    if (error || !user) {
      if (error.code === '23505') {
        throw new ConflictException('Invalid input data');
      } else {
        throw new InternalServerErrorException('Could not create the user');
      }
    }

    return user;
  }

  async findAll(): Promise<UserDto[]> {
    const { data: users, error } = await this.supabaseService
      .getClient()
      .from('custom_users')
      .select('id, email, username, socialProfilePictureUrl');

    if (error || !users) {
      throw new InternalServerErrorException('Could not find the users');
    }

    return users;
  }

  async findOne(id: number): Promise<UserDto> {
    const { data: user, error } = await this.supabaseService
      .getClient()
      .from('custom_users')
      .select('id, email, username, socialProfilePictureUrl')
      .eq('id', id)
      .single();

    if (error || !user) {
      throw new NotFoundException(`User with id ${id} not found`);
    }

    return user;
  }

  async update(id: number, updateUserDto: UserUpdateDto): Promise<UserDto> {
    const existingUser = await this.findOne(id);

    const user = { ...existingUser, ...updateUserDto };

    const { data: updatedUser, error: updatedError } =
      await this.supabaseService
        .getClient()
        .from('custom_users')
        .update(user)
        .eq('id', id)
        .select('id, email, username,  socialProfilePictureUrl')
        .single();

    if (updatedError || !updatedUser) {
      throw new InternalServerErrorException('Could not update the user');
    }

    return updatedUser;
  }

  async findBySocialMediaToken(
    socialMedia: SocialMedia,
    token: string,
  ): Promise<UserDto> {
    const { data: user, error } = await this.supabaseService
      .getClient()
      .from('custom_users')
      .select('id, email, username, socialProfilePictureUrl')
      .eq(`${socialMedia}Token`, token);

    if (error) {
      throw new InternalServerErrorException(`Could not search for user`);
    }

    if (!user) {
      return null;
    }

    return user[0];
  }

  async findByEmail(email: string): Promise<UserDto> {
    const { data: user, error } = await this.supabaseService
      .getClient()
      .from('custom_users')
      .select('id, email, username, socialProfilePictureUrl')
      .eq('email', email)
      .single();

    if (error || !user) {
      throw new NotFoundException(`User with given email not found`);
    }

    return user;
  }

  async profile(id: number): Promise<UserProfileDto> {
    const { data: user, error } = await this.supabaseService
      .getClient()
      .rpc('get_profile_by_id', {
        user_id_input: id,
      })
      .single();

    if (error || !user) {
      throw new NotFoundException(`User profile with id ${id} not found`);
    }

    return {
      id: user.id,
      email: user.email,
      username: user.username,
      socialProfilePictureUrl: user.socialProfilePictureUrl,
      publicationsCount: user.postsCount,
      reactionsCount: user.reactionsCount,
    };
  }
}
