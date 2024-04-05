import { Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Post } from '@/resources/post/entities/post.entity';
import { User } from '@/resources/user/entities/user.entity';

@Entity({ name: 'post-dislikes' })
export class PostDislike {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Post, (post) => post.dislikes)
  post: Post;

  @ManyToOne(() => User, (user) => user.postDislikes)
  user: User;
}
