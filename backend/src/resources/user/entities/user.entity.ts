import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { AutoMap } from '@automapper/classes';
import { Post } from '@/resources/post/entities/post.entity';
import { BaseEntity } from '@/infrastructure/models/entities/base.model';
import { PostReaction } from '@/resources/post/entities/post-reaction.entity';

@Entity({ name: 'users' })
export class User extends BaseEntity {
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

  @OneToMany(() => PostReaction, (postReaction) => postReaction.user)
  postReactions: PostReaction[];
}
