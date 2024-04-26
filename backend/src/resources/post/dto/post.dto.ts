import { UserDto } from '@/resources/user/dto/user.dto';
import { CommentDto } from '@/resources/comment/dto/comment.dto';
import { AutoMap } from '@automapper/classes';
import { Media } from '@/resources/media/entities/media.entity';
import { ApiProperty } from '@nestjs/swagger';
import { PointDto } from '@/infrastructure/models/dto/point.dto';
import { Reaction, ReactionType } from '@/shared/types';

type PostReactionsType = Record<ReactionType, number>;

export class PostReactionsDto implements PostReactionsType {
  @ApiProperty({ type: 'integer' })
  dislike: number = 0;

  @ApiProperty({ type: 'integer' })
  like: number = 0;

  @ApiProperty({ name: 'userReaction', enum: Reaction, required: false })
  userReaction?: ReactionType;
}

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

  @ApiProperty({ type: PostReactionsDto })
  reactions: PostReactionsDto;

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
