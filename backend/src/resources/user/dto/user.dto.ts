import { AutoMap } from '@automapper/classes';
import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsOptional, IsString } from 'class-validator';

export class UserDto {
  @AutoMap()
  @IsOptional()
  @IsNumber()
  id?: number;

  @AutoMap()
  @IsNotEmpty()
  @IsString()
  email: string;

  @AutoMap()
  @IsNotEmpty()
  @IsString()
  firstName: string;

  @AutoMap()
  @IsNotEmpty()
  @IsString()
  lastName: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  apple_token?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  google_token?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  facebook_token?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  socialProfilePictureUrl?: string;
}
