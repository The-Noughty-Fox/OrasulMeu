import {
  Column,
  Entity,
  JoinTable,
  ManyToMany,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from '@/resources/user/entities/user.entity';
import { PostLike } from '@/resources/post/entities/post-like.entity';
import { PostDislike } from '@/resources/post/entities/post-dislike.entity';
import { Comment } from '@/resources/comment/entities/comment.entity';
import { AutoMap } from '@automapper/classes';
import { Media } from '@/resources/media/entities/media.entity';
import { PostMedia } from '@/resources/media/entities/post-media.entity';

@Entity({ name: 'posts' })
export class Post {
  @PrimaryGeneratedColumn()
  @AutoMap()
  id: number;

  @Column({ default: '' })
  @AutoMap()
  title: string;

  @Column({ default: '', type: 'text' })
  @AutoMap()
  content: string;

  @OneToMany(() => PostLike, (postLike) => postLike.post)
  likes?: PostLike[];

  @OneToMany(() => PostDislike, (postDislike) => postDislike.post)
  dislikes?: PostDislike[];

  @ManyToOne(() => User, (user) => user.posts)
  @AutoMap()
  author: User;

  @OneToMany(() => Comment, (comment) => comment.post)
  comments: Comment[];

  @OneToMany(() => PostMedia, (postMedia) => postMedia.post, { cascade: true })
  @JoinTable({ name: 'post_media' })
  postMedia?: PostMedia[];

  media: Media[];
}
