import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { CreateCommentDto } from './dto/create-comment.dto';
import { UpdateCommentDto } from './dto/update-comment.dto';
import { SupabaseClient } from '@supabase/supabase-js';
import { SupabaseService } from '../supabase/supabase.service';
import { CommentDto } from './dto/comment.dto';
import { UserService } from '../user/user.service';

@Injectable()
export class CommentService {
  private readonly supabase: SupabaseClient;

  constructor(
    private readonly supabaseService: SupabaseService,
    private readonly userService: UserService,
  ) {
    this.supabase = this.supabaseService.getClient();
  }

  async findByPostId(postId: number): Promise<CommentDto[]> {
    const { data: commentsData, error } = await this.supabase
      .from('comments')
      .select('id, body, userId')
      .eq('postId', postId);

    if (error) {
      throw new InternalServerErrorException(
        'Could not find comments for requested post',
      );
    }

    if (!commentsData || commentsData.length === 0) {
      return [];
    }

    const comments = await Promise.all(
      commentsData.map(async (comment) => {
        return {
          id: comment.id,
          body: comment.body,
          author: await this.userService.findOne(comment.userId),
        } as CommentDto;
      }),
    );

    return comments;
  }

  create(createCommentDto: CreateCommentDto) {
    return 'This action adds a new comment';
  }

  findAll() {
    return `This action returns all comment`;
  }

  findOne(id: number) {
    return `This action returns a #${id} comment`;
  }

  update(id: number, updateCommentDto: UpdateCommentDto) {
    return `This action updates a #${id} comment`;
  }

  remove(id: number) {
    return `This action removes a #${id} comment`;
  }
}
