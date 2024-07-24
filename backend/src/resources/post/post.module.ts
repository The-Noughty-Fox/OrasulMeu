import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PostController } from './post.controller';
import { UserProfile } from '@/resources/user/user.profile';
import { MediaModule } from '@/resources/media/media.module';
import { SupabaseModule } from '../supabase/supabase.module';

@Module({
  imports: [MediaModule, SupabaseModule],
  controllers: [PostController],
  providers: [PostService, UserProfile],
  exports: [PostService],
})
export class PostModule {}
