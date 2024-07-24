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
      .select('id');

    if (postError || !postId || postId.length === 0) {
      throw new InternalServerErrorException('Could not create the post');
    }

    try {
      const post = await this.findOne(postId[0].id, userId);
      return post as PostDto;
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

    if (error) {
      throw new InternalServerErrorException('Could not find the posts');
    }

    const { data: postsCount, error: countError } = await this.supabaseService
      .getClient()
      .rpc('count_posts');

    if (countError) {
      throw new InternalServerErrorException('Could not count the posts');
    }

    return {
      data: posts ? (posts as PostDto[]) : [],
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
      .rpc('get_posts_ordered_by_reactions', {
        page_input: page,
        limit_input: limit,
        user_id_input: userId,
      });

    if (error) {
      throw new InternalServerErrorException('Could not find the posts');
    }

    const { data: postsCount, error: countError } = await this.supabaseService
      .getClient()
      .rpc('count_posts');

    if (countError) {
      throw new InternalServerErrorException('Could not count the posts');
    }

    return {
      data: posts ? (posts as PostDto[]) : [],
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
      .rpc('get_posts_for_user', {
        user_id_input: userId,
        page_input: page,
        limit_input: limit,
      });

    if (error) {
      throw new InternalServerErrorException("Could not find user's posts");
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
      data: posts ? (posts as PostDto[]) : [],
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

    if (error) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!post || post.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    return post as PostDto;
  }

  async update(
    id: number,
    updatePostDto: UpdatePostDto,
    userId: number,
  ): Promise<PostDto> {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('title, content, locationAddress, location')
        .eq('id', id)
        .eq('userId', userId);

    if (existingError) {
      throw new InternalServerErrorException(
        `Post with id ${id} not found or user not allowed to edit post`,
      );
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(
        `Post with id ${id} not found or user not allowed to edit post`,
      );
    }

    const post = {
      title: updatePostDto.title || existingPost[0].title,
      content: updatePostDto.content || existingPost[0].content,
      locationAddress:
        updatePostDto.locationAddress || existingPost[0].locationAddress,
      location:
        updatePostDto.location &&
        updatePostDto.location.longitude &&
        updatePostDto.location.latitude
          ? `POINT(${updatePostDto.location.longitude} ${updatePostDto.location.latitude})`
          : existingPost[0].location,
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
      return updatedPost as PostDto;
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
      .eq('userId', userId);

    if (existingError) {
      throw new InternalServerErrorException(
        `Post with id ${id} not found or user not allowed to delete post`,
      );
    }

    if (!post || post.length === 0) {
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

  async react(
    postId: number,
    userId: number,
    reaction: ReactionType,
  ): Promise<PostDto> {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId);

    if (existingError) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(postId));
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
        .select();

    if (postReactionError || !postReaction || postReaction.length === 0) {
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

  async retrieveReaction(postId: number, userId: number): Promise<PostDto> {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId);

    if (existingError) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(postId));
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

  async addMedia(
    postId: number,
    userId: number,
    files: Express.Multer.File[],
  ): Promise<PostDto> {
    const { data: existingPost, error: existingError } =
      await this.supabaseService
        .getClient()
        .from('posts')
        .select('id')
        .eq('id', postId);

    if (existingError) {
      throw new InternalServerErrorException('Could not add media post');
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(postId));
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
      return updatedPost as PostDto;
    } catch (e) {
      throw new InternalServerErrorException('Could not upload media');
    }
  }
}
