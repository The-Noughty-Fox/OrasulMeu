import { ApiProperty } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';

export class UserUpdateDto {
  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  email?: string;

  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  firstName?: string;

  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  lastName?: string;

  @IsString()
  @IsOptional()
  @ApiProperty({ required: false })
  socialProfilePictureUrl?: string;
}
