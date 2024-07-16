import { Injectable, NotFoundException } from '@nestjs/common';
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
    private supabaseService: SupabaseService,
  ) {}

  async create(createPostDto: CreatePostDto) {
    const supabase = this.supabaseService.getClient();

    const { title, content, locationAddress, location } = createPostDto;

    const { data, error } = await supabase
      .from('posts')
      .insert([
        {
          title,
          content,
          location_address: locationAddress,
          author_id: 'ae8003be-db22-4bc2-9508-e160dcea0508',
          location: `POINT(${location.longitude} ${location.latitude})`,
        },
      ])
      .select();

    console.log(data, error);

    return 'test';
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
