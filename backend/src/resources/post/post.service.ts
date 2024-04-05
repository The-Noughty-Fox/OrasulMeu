import {
  BadRequestException,
  Injectable,
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
import { PostLike } from '@/resources/post/entities/post-like.entity';
import { PostDislike } from '@/resources/post/entities/post-dislike.entity';

@Injectable()
export class PostService {
  constructor(
    @InjectRepository(Post) private readonly repository: Repository<Post>,
    @InjectRepository(User) private readonly userRepository: Repository<User>,
    @InjectRepository(PostLike)
    private readonly postLikeRepository: Repository<PostLike>,
    @InjectRepository(PostDislike)
    private readonly postDislikeRepository: Repository<PostDislike>,
    @InjectRepository(PostMedia)
    private readonly postMediaRepository: Repository<PostMedia>,
    @InjectMapper()
    private readonly mapper: Mapper,
    private mediaService: MediaService,
  ) {}

  async create(createPostDto: CreatePostDto, userId: number) {
    const postEntity = this.repository.create(createPostDto);
    postEntity.author = await this.userRepository.findOne({
      where: { id: userId },
    });
    await this.repository.save(postEntity);
    return this.mapper.map(postEntity, Post, PostDto);
  }

  async findAll() {
    const postEntities = await this.repository.find({
      relations: [
        'author',
        'postMedia',
        'postMedia.media',
        'likes',
        'dislikes',
      ],
    });

    return this.mapper.mapArray(postEntities, Post, PostDto);
  }

  async findOne(id: number) {
    const post = await this.repository.findOne({
      where: { id },
      relations: [
        'author',
        'postMedia',
        'postMedia.media',
        'likes',
        'dislikes',
      ],
    });

    if (!post) {
      throw new NotFoundException(`Post with id ${id} not found`);
    }

    return this.mapper.map(post, Post, PostDto);
  }

  async update(id: number, updatePostDto: UpdatePostDto) {
    const post = await this.repository.findOne({
      where: { id },
      relations: ['author', 'postMedia', 'postMedia.media'],
    });

    if (!post) {
      throw new NotFoundException(`Post with id ${id} not found`);
    }

    const updatedPost = await this.repository.save(
      Object.assign(post, updatePostDto),
    );

    return this.mapper.map(updatedPost, Post, PostDto);
  }

  async remove(id: number) {
    const deletionResult = await this.repository.delete(id);

    if (deletionResult.affected === 0) {
      throw new NotFoundException(`Post with id ${id} not found`);
    }

    return true;
  }

  async like(id: number, userId: number) {
    const post = await this.repository.findOne({
      where: { id },
    });

    if (!post) {
      throw new NotFoundException(`Post with id ${id} not found`);
    }

    const existingPostLike = await this.postLikeRepository.findOne({
      where: { post: { id } },
    });

    if (existingPostLike) {
      throw new BadRequestException('You have already liked this post');
    }

    const postLike = this.postLikeRepository.create({
      post,
      user: { id: userId },
    });
    await this.postLikeRepository.save(postLike);

    return true;
  }

  async dislike(id: number, userId: number) {
    const post = await this.repository.findOne({
      where: { id },
    });

    if (!post) {
      throw new NotFoundException(`Post with id ${id} not found`);
    }

    const existingPostLike = await this.postDislikeRepository.findOne({
      where: { post: { id } },
    });

    if (existingPostLike) {
      throw new BadRequestException('You have already disliked this post');
    }

    const postDislike = this.postDislikeRepository.create({
      post,
      user: { id: userId },
    });
    await this.postDislikeRepository.save(postDislike);

    return true;
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
}
