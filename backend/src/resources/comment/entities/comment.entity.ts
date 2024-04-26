import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { User } from '@/resources/user/entities/user.entity';
import { Post } from '@/resources/post/entities/post.entity';
import { BaseEntity } from '@/infrastructure/models/entities/base.model';

@Entity()
export class Comment extends BaseEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column('text')
  body: string;

  @ManyToOne(() => Post, (post) => post.comments)
  post: Post;

  @ManyToOne(() => User)
  author: User;
}
