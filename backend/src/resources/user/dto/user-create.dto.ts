import { AutoMap } from '@automapper/classes';
import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class UserCreateDto {
  @AutoMap()
  @IsNotEmpty()
  @IsString()
  @ApiProperty({ required: true })
  email: string;

  @AutoMap()
  @IsNotEmpty()
  @IsString()
  @ApiProperty({ required: true })
  firstName: string;

  @AutoMap()
  @IsNotEmpty()
  @IsString()
  @ApiProperty({ required: true })
  lastName: string;

  @AutoMap()
  @IsString()
  @ApiProperty({ required: false })
  apple_token?: string;

  @AutoMap()
  @IsString()
  @ApiProperty({ required: false })
  google_token?: string;

  @AutoMap()
  @IsString()
  @ApiProperty({ required: false })
  facebook_token?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  @ApiProperty({ required: false })
  socialProfilePictureUrl?: string;
}
