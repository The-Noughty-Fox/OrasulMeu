import { UserDto } from '@/resources/user/dto/user.dto';
import { CommentDto } from '@/resources/comment/dto/comment.dto';
import { AutoMap } from '@automapper/classes';
import { Media } from '@/resources/media/entities/media.entity';

export class PostDto {
  @AutoMap()
  id: number;

  @AutoMap()
  title: string;

  @AutoMap()
  content: string;

  @AutoMap()
  author: UserDto;

  likes: number = 0;

  dislikes: number = 0;

  comments: CommentDto[];

  media?: Media[];
}
