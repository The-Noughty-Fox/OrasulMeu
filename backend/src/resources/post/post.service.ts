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
import { PostDto, PostReactionsDto } from '@/resources/post/dto/post.dto';
import { Post } from '@/resources/post/entities/post.entity';
import { InjectMapper } from '@automapper/nestjs';
import { User } from '@/resources/user/entities/user.entity';
import { MediaService } from '@/resources/media/media.service';
import { PostMedia } from '@/resources/media/entities/post-media.entity';
import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { PaginationResultDto } from '@/infrastructure/models/dto/pagination-result.dto';
import { POST_NOT_FOUND } from '@/infrastructure/messages';
import { PostReaction } from '@/resources/post/entities/post-reaction.entity';
import { Reaction, ReactionType } from '@/shared/types';
import { SupabaseService } from '@/resources/supabase/supabase.service';
import { SupabaseClient } from '@supabase/supabase-js';
import { UserService } from '../user/user.service';
import { CommentService } from '../comment/comment.service';

const fullPostRelations = [
  'author',
  'postMedia',
  'postMedia.media',
  'reactions',
  'reactions.post',
  'reactions.user',
];

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

  async create(createPostDto: CreatePostDto, userId: number) {
    const { title, content, locationAddress, location } = createPostDto;

    const { data: postData, error: postError } = await this.supabase
      .from('posts')
      .insert([
        {
          title,
          content,
          locationAddress,
          userId,
          location: `POINT(${location.longitude} ${location.latitude})`,
        },
      ])
      .select('id, title, content, locationAddress, location, createdAt');

    if (postError || !postData || postData.length === 0) {
      throw new InternalServerErrorException('Could not create the post');
    }

    const reactions: PostReactionsDto = {
      like: await this.findPostReactions(postData[0].id, Reaction.Like),
      dislike: await this.findPostReactions(postData[0].id, Reaction.Dislike),
      userReaction: null,
    };

    const post = postData[0];
    post['author'] = await this.userService.findOne(userId);
    post['reactions'] = reactions;
    post['comments'] = await this.commentService.findByPostId(post.id);
    post['media'] = await this.findMedia(post.id);

    console.log(post);

    return post;
  }

  async findPostReactions(postId: number, reactionType: ReactionType) {
    const { data: reaction, error } = await this.supabase.rpc(
      'get_reaction_count',
      {
        post_id_input: postId,
        reaction_input: reactionType,
      },
    );

    if (error) {
      throw new InternalServerErrorException('Could not get post reactions');
    }

    return reaction;
  }

  async findMedia(postId: number) {
    const { data: media, error } = await this.supabase.rpc(
      'get_media_by_post_id',
      {
        post_id_input: postId,
      },
    );

    if (error) {
      throw new InternalServerErrorException('Could not get post media');
    }

    if (!media || media.length === 0) {
      return [];
    }

    return media;
  }

  async findAll(
    paginationQuery: PaginationQueryDto,
  ): Promise<PaginationResultDto<PostDto>> {
    const { page = 1, limit = 25 } = paginationQuery;
    const [postEntities, total] = await this.repository.findAndCount({
      relations: fullPostRelations,
      skip: (page - 1) * limit,
      take: limit,
    });

    return {
      page,
      limit,
      total,
      data: this.mapper.mapArray(postEntities, Post, PostDto),
    };
  }

  async findOne(id: number) {
    const post = await this.repository.findOne({
      where: { id },
      relations: fullPostRelations,
    });

    const supabase = this.supabaseService.getClient();

    const { data: posts, error } = await supabase
      .from('posts')
      .select('*')
      .eq('id', id);

    console.log(posts, error);

    console.log('we are here');

    if (!post) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    return this.mapper.map(post, Post, PostDto);
  }

  async update(id: number, updatePostDto: UpdatePostDto) {
    const post = await this.repository.findOne({
      where: { id },
      relations: fullPostRelations,
    });

    if (!post) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    const updatedPost = await this.repository.save(
      Object.assign(post, updatePostDto),
    );

    return this.mapper.map(updatedPost, Post, PostDto);
  }

  async remove(id: number) {
    const deletionResult = await this.repository.delete(id);

    if (deletionResult.affected === 0) {
      throw new NotFoundException(POST_NOT_FOUND(id));
    }

    return true;
  }

  async react(
    postId: number,
    userId: number,
    reaction: ReactionType,
  ): Promise<PostDto> {
    let post = await this.repository.findOne({
      where: { id: postId },
      relations: fullPostRelations,
    });

    if (!post) {
      throw new NotFoundException(POST_NOT_FOUND(postId));
    }

    const existingPostReaction = await this.postReactionRepository.findOne({
      where: { post: { id: postId }, user: { id: userId } },
    });

    if (existingPostReaction) {
      await this.postReactionRepository.delete(existingPostReaction.id);
      post = {
        ...post,
        reactions: post.reactions.filter(
          (r) => r.user.id !== userId && r.post.id !== postId,
        ),
      };
    }

    if (
      (existingPostReaction && existingPostReaction.reaction !== reaction) ||
      !existingPostReaction
    ) {
      const postReaction = this.postReactionRepository.create({
        post,
        user: { id: userId },
        reaction,
      });
      await this.postReactionRepository.save(postReaction);
      post = {
        ...post,
        reactions: [...(post.reactions || []), postReaction],
      };
    }

    const postDto = this.mapper.map(post, Post, PostDto);
    return {
      ...postDto,
      reactions: {
        ...postDto.reactions,
        userReaction:
          existingPostReaction?.reaction === reaction ? null : reaction,
      },
    };
  }

  async addMedia(postId: number, files: Express.Multer.File[]) {
    const media = await this.mediaService.create(files);
    const post = await this.repository.findOne({
      where: { id: postId },
      relations: ['postMedia'],
    });
    post.postMedia = [
      ...post.postMedia,
      ...media.map((m) => this.postMediaRepository.create({ media: m, post })),
    ];
    return this.mapper.map(await this.repository.save(post), Post, PostDto);
  }

  async getMyPosts(userId: number) {
    const posts = await this.repository.find({
      where: { author: { id: userId } },
      relations: fullPostRelations,
    });

    return this.mapper.mapArray(posts, Post, PostDto);
  }
}
