import { ApiProperty } from '@nestjs/swagger';
import { PointDto } from '@/infrastructure/models/dto/point.dto';
import { AutoMap } from '@automapper/classes';
import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreatePostDto {
  @AutoMap()
  @IsNotEmpty()
  @IsString()
  @ApiProperty({ required: true })
  title: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  @ApiProperty({ required: false })
  content?: string;

  @AutoMap()
  @IsOptional()
  @IsString()
  @ApiProperty({ required: false })
  locationAddress?: string;

  @ApiProperty({ type: PointDto, required: false })
  @IsOptional()
  @AutoMap()
  location?: PointDto;
}
