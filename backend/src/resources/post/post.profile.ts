import { AutomapperProfile, InjectMapper } from '@automapper/nestjs';
import { createMap, forMember, mapFrom, Mapper } from '@automapper/core';
import { Injectable } from '@nestjs/common';
import { Post } from '@/resources/post/entities/post.entity';
import { PostDto, PostReactionsDto } from '@/resources/post/dto/post.dto';

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
          (d) => d.reactions,
          mapFrom((s) =>
            s.reactions?.reduce<PostReactionsDto>(
              (acc, item) => {
                if (!acc[item.reaction]) {
                  acc[item.reaction] = 1;
                } else {
                  acc[item.reaction] += 1;
                }

                return acc;
              },
              {
                like: 0,
                dislike: 0,
                userReaction: null,
              },
            ),
          ),
        ),
        forMember(
          (d) => d.location,
          mapFrom((s) =>
            s.location
              ? {
                  longitude: s.location.coordinates[0],
                  latitude: s.location.coordinates[1],
                }
              : null,
          ),
        ),
      );
    };
  }
}
