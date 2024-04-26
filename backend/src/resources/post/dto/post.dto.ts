import { UserDto } from '@/resources/user/dto/user.dto';
import { CommentDto } from '@/resources/comment/dto/comment.dto';
import { AutoMap } from '@automapper/classes';
import { Media } from '@/resources/media/entities/media.entity';
import { ApiProperty } from '@nestjs/swagger';
import { PointDto } from '@/infrastructure/models/dto/point.dto';

export class PostDto {
  @AutoMap()
  @ApiProperty({ type: 'integer' })
  id: number;

  @AutoMap()
  @ApiProperty()
  title: string;

  @AutoMap()
  @ApiProperty()
  content: string;

  @AutoMap()
  @ApiProperty()
  author: UserDto;

  @ApiProperty({ type: 'integer' })
  likes: number = 0;

  @ApiProperty({ type: 'integer' })
  dislikes: number = 0;

  @ApiProperty({ type: CommentDto })
  comments: CommentDto[];

  @ApiProperty({ type: Media, isArray: true })
  media?: Media[];

  @AutoMap()
  createDate: Date;

  @AutoMap()
  @ApiProperty()
  locationAddress: string;

  @ApiProperty({ type: PointDto })
  @AutoMap()
  location: PointDto;
}
