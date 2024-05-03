import { AutoMap } from '@automapper/classes';
import { ApiProperty } from '@nestjs/swagger';
import { UserDto } from '@/resources/user/dto/user.dto';

export class UserProfileDto extends UserDto {
  @AutoMap()
  @ApiProperty({ required: true, type: 'integer' })
  reactionsCount: number;

  @AutoMap()
  @ApiProperty({ required: true, type: 'integer' })
  publicationsCount: number;
}
