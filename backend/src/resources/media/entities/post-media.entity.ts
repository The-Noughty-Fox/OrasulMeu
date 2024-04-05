import { Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Post } from '@/resources/post/entities/post.entity';
import { Media } from '@/resources/media/entities/media.entity';

@Entity({ name: 'post-media' })
export class PostMedia {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Post, (post) => post.media)
  post: Post;

  @ManyToOne(() => Media)
  media: Media;
}
