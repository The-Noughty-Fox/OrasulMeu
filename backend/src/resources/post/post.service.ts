import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';
import { PostDto } from '@/resources/post/dto/post.dto';
import { MediaService } from '@/resources/media/media.service';
import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { PaginationResultDto } from '@/infrastructure/models/dto/pagination-result.dto';
import { POST_NOT_FOUND } from '@/infrastructure/messages';
import { ReactionType } from '@/shared/types';
import { SupabaseService } from '@/resources/supabase/supabase.service';
import { CUSTOM_ERROR_CODES } from '@/infrastructure/error-codes/custom-error-codes';
import { timestamptzToDate } from '@/helpers/timestamptz-to-date';
import { mapToReaction } from '@/helpers/map-to-reaction';
import { mapToMediaType } from '@/helpers/map-to-media-type';

@Injectable()
export class PostService {
  constructor(
    private mediaService: MediaService,
    private readonly supabaseService: SupabaseService,
  ) {}

  async create(createPostDto: CreatePostDto, userId: number): Promise<PostDto> {
    const { data: postId, error: postError } = await this.supabaseService
      .getClient()
      .from('posts')
      .insert([
        {
          title: createPostDto.title,
          content: createPostDto.content,
          locationAddress: createPostDto.locationAddress,
          userId,
          location:
            createPostDto.location &&
            createPostDto.location.longitude &&
            createPostDto.location.latitude
              ? `POINT(${createPostDto.location.longitude} ${createPostDto.location.latitude})`
              : null,
        },
      ])
      .select('id')
      .single();

    if (postError || !postId) {
      throw new InternalServerErrorException('Could not create the post');
    }

    try {
      const post = await this.findOne(postId.id, userId);
      return post;
    } catch (e) {
      throw new InternalServerErrorException('Could not create the post');
    }
  }

  async findAll(
    paginationQuery: PaginationQueryDto,
    userId: number,
  ): Promise<PaginationResultDto<PostDto>> {
    const { page = 1, limit = 10 } = paginationQuery;

    const { data: posts, error } = await this.supabaseService
      .getClient()
      .rpc('get_posts', {
        page_input: page,
        limit_input: limit,
        user_id_input: userId,
      });

    if (error && error.code === CUSTOM_ERROR_CODES.USER_NOT_FOUND) {
      throw new NotFoundException(`User with id ${userId} not found`);
    } else if (error) {
      throw new InternalServerErrorException('Could not get the posts');
    }

    const { data: postsCount, error: countError } = await this.supabaseService
      .getClient()
      .rpc('count_posts');

    if (countError) {
      throw new InternalServerErrorException('Could not count the posts');
    }

    return {
      data: posts.map((post) => ({
        ...post,
        createdAt: timestamptzToDate(post.createdAt),
        reactions: {
          ...post.reactions,
          userReaction: mapToReaction(post.reactions.userReaction),
        },
        media: post.media.map((mediaItem) => ({
          ...mediaItem,
          type: mapToMediaType(mediaItem.type),
        })),
      })),
      page: page,
      limit: limit,
      total: postsCount,
    };
  }

  async findAllReactionCountOrder(
    paginationQuery: PaginationQueryDto,
    userId: number,
  ): Promise<PaginationResultDto<PostDto>> {
    const { page = 1, limit = 10 } = paginationQuery;

    const { data: posts, error } = await this.supabaseService
      .getClient()
      .rpc('get_posts_by_reactions', {
        page_input: page,
        limit_input: limit,
        user_id_input: userId,
      });

    if (error && error.code === CUSTOM_ERROR_CODES.USER_NOT_FOUND) {
      throw new NotFoundException(`User with id ${userId} not found`);
    } else if (error) {
      throw new InternalServerErrorException('Could not find the posts');
    }

    const { data: postsCount, error: countError } = await this.supabaseService
      .getClient()
      .rpc('count_posts');

    if (countError) {
      throw new InternalServerErrorException('Could not count the posts');
    }

    return {
      data: posts.map((post) => ({
        ...post,
        createdAt: timestamptzToDate(post.createdAt),
        reactions: {
          ...post.reactions,
          userReaction: mapToReaction(post.reactions.userReaction),
        },
        media: post.media.map((mediaItem) => ({
          ...mediaItem,
          type: mapToMediaType(mediaItem.type),
        })),
      })),
      page: page,
      limit: limit,
      total: postsCount,
    };
  }

  async findMyPosts(
    userId: number,
    paginationQuery: PaginationQueryDto,
  ): Promise<PaginationResultDto<PostDto>> {
    const { page = 1, limit = 10 } = paginationQuery;

    const { data: posts, error } = await this.supabaseService
      .getClient()
      .rpc('get_user_posts', {
        user_id_input: userId,
        page_input: page,
        limit_input: limit,
      });

    if (error && error.code === CUSTOM_ERROR_CODES.USER_NOT_FOUND) {
      throw new NotFoundException(`User with id ${userId} not found`);
    } else if (error) {
      throw new InternalServerErrorException('Could not find the posts');
    }

    const { data: postsCount, error: countError } = await this.supabaseService
      .getClient()
      .rpc('count_posts_for_user', {
        user_id_input: userId,
      });

    if (countError) {
      console.error(countError);
      throw new InternalServerErrorException('Could not count the posts');
    }

    return {
      data: posts.map((post) => ({
        ...post,
        createdAt: timestamptzToDate(post.createdAt),
        reactions: {
          ...post.reactions,
          userReaction: mapToReaction(post.reactions.userReaction),
        },
        media: post.media.map((mediaItem) => ({
          ...mediaItem,
          type: mapToMediaType(mediaItem.type),
        })),
      })),
      page: page,
      limit: limit,
      total: postsCount,
    };
  }

  async findOne(id: number, userId: number): Promise<PostDto> {
    const { data: post, error } = await this.supabaseService
      .getClient()
      .rpc('get_post', {
        post_id_input: id,
        user_id_input: userId,
      });

    if (error && error.code === CUSTOM_ERROR_CODES.POST_NOT_FOUND) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    } else if (error && error.code === CUSTOM_ERROR_CODES.USER_NOT_FOUND) {
      throw new NotFoundException(`User with id ${userId} not found`);
    } else if (error || !post) {
      throw new InternalServerErrorException('Could not find the post');
    }

    return {
      ...post,
      createdAt: timestamptzToDate(post.createdAt),
      reactions: {
        ...post.reactions,
        userReaction: mapToReaction(post.reactions.userReaction),
      },
      media: post.media.map((mediaItem) => ({
        ...mediaItem,
        type: mapToMediaType(mediaItem.type),
      })),
    };
  }

  async update(id: number, updatePostDto: UpdatePostDto, userId: number) {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('title, content, locationAddress, location')
        .eq('id', id)
        .eq('userId', userId)
        .single();

    if (existingError || !existingPost) {
      throw new NotFoundException(
        `Post with id ${id} not found or user not allowed to edit post`,
      );
    }

    const post = {
      title: updatePostDto.title || existingPost.title,
      content: updatePostDto.content || existingPost.content,
      locationAddress:
        updatePostDto.locationAddress || existingPost.locationAddress,
      location:
        updatePostDto.location &&
        updatePostDto.location.longitude &&
        updatePostDto.location.latitude
          ? `POINT(${updatePostDto.location.longitude} ${updatePostDto.location.latitude})`
          : existingPost.location,
    };

    const { error: updatedError } = await this.supabaseService
      .getClient()
      .from('posts')
      .update(post)
      .eq('id', id);

    if (updatedError) {
      throw new InternalServerErrorException('Could not update the post');
    }

    try {
      const updatedPost = await this.findOne(id, userId);
      return updatedPost;
    } catch (e) {
      throw new InternalServerErrorException('Could not update the post');
    }
  }

  async remove(id: number, userId: number): Promise<boolean> {
    const { data: post, error: existingError } = await this.supabaseService
      .getClient()
      .from('posts')
      .select('id')
      .eq('id', id)
      .eq('userId', userId)
      .single();

    if (existingError || !post) {
      throw new NotFoundException(
        `Post with id ${id} not found or user not allowed to delete post`,
      );
    }

    const { error } = await this.supabaseService
      .getClient()
      .from('posts')
      .delete()
      .eq('id', id)
      .eq('userId', userId);

    if (error) {
      throw new InternalServerErrorException('Could not delete the post');
    }

    return true;
  }

  async react(postId: number, userId: number, reaction: ReactionType) {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId)
        .single();

    if (existingError || !existingPost) {
      throw new NotFoundException('Could not find the post');
    }

    const { data: existingPostReaction, error: existingPostReactionError } =
      await this.supabaseService
        .getClient()
        .from('post_reactions')
        .select('id')
        .eq('postId', postId)
        .eq('userId', userId);

    if (existingPostReactionError) {
      throw new InternalServerErrorException(
        'Could not react to the post. Please try again',
      );
    }

    if (existingPostReaction && existingPostReaction.length > 0) {
      const { error: deleteError } = await this.supabaseService
        .getClient()
        .from('post_reactions')
        .delete()
        .eq('id', existingPostReaction[0].id);

      if (deleteError) {
        throw new InternalServerErrorException(
          'Could not react to the post. Please try again',
        );
      }
    }

    const { data: postReaction, error: postReactionError } =
      await this.supabaseService
        .getClient()
        .from('post_reactions')
        .insert([{ reaction, postId, userId }])
        .select()
        .single();

    if (postReactionError || !postReaction) {
      throw new InternalServerErrorException(
        'Could not react to the post. Please try again',
      );
    }

    try {
      const post = await this.findOne(postId, userId);
      return post;
    } catch (e) {
      throw new InternalServerErrorException(
        'Could not react to the post. Please try again',
      );
    }
  }

  async retrieveReaction(postId: number, userId: number) {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId)
        .single();

    if (existingError || !existingPost) {
      throw new NotFoundException('Could not find the post');
    }

    const { data: existingPostReaction, error: existingPostReactionError } =
      await this.supabaseService
        .getClient()
        .from('post_reactions')
        .select('id')
        .eq('postId', postId)
        .eq('userId', userId);

    if (existingPostReactionError) {
      throw new InternalServerErrorException(
        'Could not retrieve the reaction. Please try again',
      );
    }

    if (existingPostReaction && existingPostReaction.length > 0) {
      const { error: deleteError } = await this.supabaseService
        .getClient()
        .from('post_reactions')
        .delete()
        .eq('id', existingPostReaction[0].id);

      if (deleteError) {
        throw new InternalServerErrorException(
          'Could not retrieve the reaction. Please try again',
        );
      }
    }

    try {
      const post = await this.findOne(postId, userId);
      return post;
    } catch (e) {
      throw new InternalServerErrorException(
        'Could not retrieve the reaction. Please try again',
      );
    }
  }

  async addMedia(postId: number, userId: number, files: Express.Multer.File[]) {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId)
        .eq('userId', userId)
        .single();

    if (existingError || !existingPost) {
      throw new NotFoundException('Could not add media post');
    }

    const media = await this.mediaService.create(files);

    for (const mediaFile of media) {
      const { data, error } = await this.supabaseService
        .getClient()
        .from('post_media')
        .insert([{ postId, mediaId: mediaFile.id }])
        .select('id');

      if (error || !data || data.length === 0) {
        throw new InternalServerErrorException(
          'Could not add media to the post',
        );
      }
    }

    try {
      const updatedPost = await this.findOne(postId, userId);
      return updatedPost;
    } catch (e) {
      throw new InternalServerErrorException('Could not upload media');
    }
  }
}
