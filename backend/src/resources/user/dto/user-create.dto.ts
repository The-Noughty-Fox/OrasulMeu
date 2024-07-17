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
  @IsOptional()
  @ApiProperty({ required: false })
  appleToken?: string;

  @AutoMap()
  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  googleToken?: string;

  @AutoMap()
  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  facebookToken?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  @ApiProperty({ required: false })
  socialProfilePictureUrl?: string;
}
