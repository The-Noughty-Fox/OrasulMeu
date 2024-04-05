import { AutomapperProfile, InjectMapper } from '@automapper/nestjs';
import { createMap, forMember, mapFrom, Mapper } from '@automapper/core';
import { Injectable } from '@nestjs/common';
import { Post } from '@/resources/post/entities/post.entity';
import { PostDto } from '@/resources/post/dto/post.dto';

@Injectable()
export class PostProfile extends AutomapperProfile {
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
        Post,
        PostDto,
        forMember(
          (d) => d.author,
          mapFrom((s) => s.author),
        ),
        forMember(
          (d) => d.media,
          mapFrom((s) => s.postMedia?.map((pm) => pm.media) ?? []),
        ),
        forMember(
          (d) => d.likes,
          mapFrom((s) => s.likes?.length ?? 0),
        ),
        forMember(
          (d) => d.dislikes,
          mapFrom((s) => s.dislikes?.length ?? 0),
        ),
      );
    };
  }
}
