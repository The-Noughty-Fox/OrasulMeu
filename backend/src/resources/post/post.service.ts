import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Mapper } from '@automapper/core';
import { PostDto } from '@/resources/post/dto/post.dto';
import { Post } from '@/resources/post/entities/post.entity';
import { InjectMapper } from '@automapper/nestjs';
import { User } from '@/resources/user/entities/user.entity';
import { MediaService } from '@/resources/media/media.service';
import { PostMedia } from '@/resources/media/entities/post-media.entity';
import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { PaginationResultDto } from '@/infrastructure/models/dto/pagination-result.dto';
import { POST_NOT_FOUND } from '@/infrastructure/messages';
import { PostReaction } from '@/resources/post/entities/post-reaction.entity';
import { ReactionType } from '@/shared/types';
import { SupabaseService } from '@/resources/supabase/supabase.service';
import { SupabaseClient } from '@supabase/supabase-js';
import { UserService } from '../user/user.service';
import { CommentService } from '../comment/comment.service';

// const fullPostRelations = [
//   'author',
//   'postMedia',
//   'postMedia.media',
//   'reactions',
//   'reactions.post',
//   'reactions.user',
// ];

@Injectable()
export class PostService {
  private readonly supabase: SupabaseClient;

  constructor(
    @InjectRepository(Post) private readonly repository: Repository<Post>,
    @InjectRepository(User) private readonly userRepository: Repository<User>,
    @InjectRepository(PostReaction)
    private readonly postReactionRepository: Repository<PostReaction>,
    @InjectRepository(PostMedia)
    private readonly postMediaRepository: Repository<PostMedia>,
    @InjectMapper()
    private readonly mapper: Mapper,
    private mediaService: MediaService,
    private readonly supabaseService: SupabaseService,
    private readonly userService: UserService,
    private readonly commentService: CommentService,
  ) {
    this.supabase = this.supabaseService.getClient();
  }

  async create(createPostDto: CreatePostDto, userId: number): Promise<PostDto> {
    const { data: postId, error: postError } = await this.supabase
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
      const post = await this.findOne(postId[0].id);
      return post as PostDto;
    } catch (e) {
      throw new InternalServerErrorException('Could not create the post');
    }
  }

  async findAll(
    paginationQuery: PaginationQueryDto,
  ): Promise<PaginationResultDto<PostDto>> {
    const { page = 1, limit = 10 } = paginationQuery;

    const { data: posts, error } = await this.supabase.rpc('get_posts', {
      page_input: page,
      limit_input: limit,
    });

    if (error) {
      throw new InternalServerErrorException('Could not find the posts');
    }

    const { data: postsCount, error: countError } =
      await this.supabase.rpc('count_posts');

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

    const { data: posts, error } = await this.supabase.rpc(
      'get_posts_for_user',
      {
        user_id_input: userId,
        page_input: page,
        limit_input: limit,
      },
    );

    if (error) {
      throw new InternalServerErrorException("Could not find user's posts");
    }

    const { data: postsCount, error: countError } = await this.supabase.rpc(
      'count_posts_for_user',
      {
        user_id_input: userId,
      },
    );

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

  async findOne(id: number): Promise<PostDto> {
    const { data: post, error } = await this.supabase.rpc('get_post_by_id', {
      post_id: id,
    });

    if (error) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!post || post.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    return post as PostDto;
  }

  async update(id: number, updatePostDto: UpdatePostDto): Promise<PostDto> {
    const { data: existingPost, error: existingError } = await this.supabase
      .from('posts')
      .select('title, content, locationAddress, location')
      .eq('id', id);

    if (existingError) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(id));
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

    const { error: updatedError } = await this.supabase
      .from('posts')
      .update(post)
      .eq('id', id);

    if (updatedError) {
      throw new InternalServerErrorException('Could not update the post');
    }

    try {
      const updatedPost = await this.findOne(id);
      return updatedPost as PostDto;
    } catch (e) {
      throw new InternalServerErrorException('Could not update the post');
    }
  }

  async remove(id: number): Promise<boolean> {
    const { data: post, error: existingError } = await this.supabase
      .from('posts')
      .select('id')
      .eq('id', id);

    if (existingError) {
      throw new InternalServerErrorException('Could not find the post');
    }

    if (!post || post.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    const { error } = await this.supabase.from('posts').delete().eq('id', id);

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
    const { data: existingPost, error: existingError } = await this.supabase
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
      await this.supabase
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
      const { error: deleteError } = await this.supabase
        .from('post_reactions')
        .delete()
        .eq('id', existingPostReaction[0].id);

      if (deleteError) {
        throw new InternalServerErrorException(
          'Could not react to the post. Please try again',
        );
      }
    }

    const { data: postReaction, error: postReactionError } = await this.supabase
      .from('post_reactions')
      .insert([{ reaction, postId, userId }])
      .select();

    if (postReactionError || !postReaction || postReaction.length === 0) {
      throw new InternalServerErrorException(
        'Could not react to the post. Please try again',
      );
    }

    try {
      const post = await this.findOne(postId);
      return post;
    } catch (e) {
      throw new InternalServerErrorException(
        'Could not react to the post. Please try again',
      );
    }
  }

  async retrieveReaction(postId: number, userId: number): Promise<PostDto> {
    const { data: existingPost, error: existingError } = await this.supabase
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
      await this.supabase
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
      const { error: deleteError } = await this.supabase
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
      const post = await this.findOne(postId);
      return post;
    } catch (e) {
      throw new InternalServerErrorException(
        'Could not retrieve the reaction. Please try again',
      );
    }
  }

  async addMedia(
    postId: number,
    files: Express.Multer.File[],
  ): Promise<PostDto> {
    const media = await this.mediaService.create(files);

    const { data: existingPost, error: existingError } = await this.supabase
      .from('posts')
      .select('id')
      .eq('id', postId);

    if (existingError) {
      throw new InternalServerErrorException('Could not add media post');
    }

    if (!existingPost || existingPost.length === 0) {
      throw new NotFoundException(POST_NOT_FOUND(postId));
    }

    for (const mediaFile of media) {
      const { data, error } = await this.supabase
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
      const updatedPost = await this.findOne(postId);
      return updatedPost as PostDto;
    } catch (e) {
      throw new InternalServerErrorException('Could not upload media');
    }
  }
}
