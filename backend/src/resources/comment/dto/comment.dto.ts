import { UserDto } from '@/resources/user/dto/user.dto';
import { AutoMap } from '@automapper/classes';

export class CommentDto {
  @AutoMap()
  id: number;

  @AutoMap()
  body: string;

  author: UserDto;
}
