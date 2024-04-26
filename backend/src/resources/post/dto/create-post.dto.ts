import { ApiProperty } from '@nestjs/swagger';
import { PointDto } from '@/infrastructure/models/dto/point.dto';
import { AutoMap } from '@automapper/classes';

export class CreatePostDto {
  @AutoMap()
  @ApiProperty()
  title: string;

  @AutoMap()
  @ApiProperty()
  content: string;

  @AutoMap()
  @ApiProperty()
  locationAddress: string;

  @ApiProperty({ type: PointDto })
  @AutoMap()
  location: PointDto;
}
