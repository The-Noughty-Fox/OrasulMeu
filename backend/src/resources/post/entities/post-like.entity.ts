import { Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Post } from '@/resources/post/entities/post.entity';
import { User } from '@/resources/user/entities/user.entity';

@Entity({ name: 'post-likes' })
export class PostLike {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Post, (post) => post.likes)
  post: Post;

  @ManyToOne(() => User, (user) => user.postLikes)
  user: User;
}
