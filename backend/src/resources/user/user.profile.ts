import { AutomapperProfile, InjectMapper } from '@automapper/nestjs';
import { createMap, forMember, mapFrom, Mapper } from '@automapper/core';
import { Injectable } from '@nestjs/common';
import { User } from './entities/user.entity';
import { UserDto } from './dto/user.dto';

@Injectable()
export class UserProfile extends AutomapperProfile {
  constructor(
    @InjectMapper()
    readonly mapper: Mapper,
  ) {
    super(mapper);
  }

  override get profile() {
    return (mapper) => {
      createMap(
        mapper,
        User,
        UserDto,
        forMember(
          (d) => d.lastName,
          mapFrom((s) => `${s.firstName} ${s.lastName}`),
        ),
      );
    };
  }
}
