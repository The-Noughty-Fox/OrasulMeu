import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Post } from '@/resources/post/entities/post.entity';
import { User } from '@/resources/user/entities/user.entity';

@Entity({ name: 'post-reactions' })
export class PostReaction {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Post, (post) => post.reactions)
  post: Post;

  @ManyToOne(() => User, (user) => user.postReactions)
  user: User;

  @Column()
  reaction: string;
}
