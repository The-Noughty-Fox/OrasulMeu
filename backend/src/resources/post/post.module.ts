import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PostController } from './post.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Post } from '@/resources/post/entities/post.entity';
import { PostProfile } from '@/resources/post/post.profile';
import { UserProfile } from '@/resources/user/user.profile';
import { User } from '@/resources/user/entities/user.entity';
import { UserModule } from '@/resources/user/user.module';
import { MediaModule } from '@/resources/media/media.module';
import { PostMedia } from '@/resources/media/entities/post-media.entity';
import { PostReaction } from '@/resources/post/entities/post-reaction.entity';
import { SupabaseModule } from '../supabase/supabase.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Post, User, PostMedia, PostReaction]),
    UserModule,
    MediaModule,
    SupabaseModule,
  ],
  controllers: [PostController],
  providers: [PostService, PostProfile, UserProfile],
  exports: [PostService, PostProfile],
})
export class PostModule {}
