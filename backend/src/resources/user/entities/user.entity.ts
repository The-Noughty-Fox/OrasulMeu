import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { AutoMap } from '@automapper/classes';
import { Post } from '@/resources/post/entities/post.entity';
import { PostLike } from '@/resources/post/entities/post-like.entity';
import { PostDislike } from '@/resources/post/entities/post-dislike.entity';

@Entity({ name: 'users' })
export class User {
  @PrimaryGeneratedColumn()
  @AutoMap()
  id: number;

  @Column()
  @AutoMap()
  email: string;

  @Column()
  @AutoMap()
  firstName: string;

  @Column()
  @AutoMap()
  lastName: string;

  @Column({ nullable: true })
  @AutoMap()
  apple_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  google_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  facebook_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  socialProfilePictureUrl?: string;

  @OneToMany(() => Post, (post) => post.author)
  posts: Post[];

  @OneToMany(() => PostLike, (post) => post.user)
  postLikes: PostLike[];

  @OneToMany(() => PostDislike, (post) => post.user)
  postDislikes: PostDislike[];
}
