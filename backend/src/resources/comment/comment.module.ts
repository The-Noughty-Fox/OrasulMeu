import { Module } from '@nestjs/common';
import { CommentService } from './comment.service';
import { CommentController } from './comment.controller';
import { SupabaseModule } from '../supabase/supabase.module';
import { UserModule } from '../user/user.module';

@Module({
  imports: [SupabaseModule, UserModule],
  controllers: [CommentController],
  providers: [CommentService],
  exports: [CommentService],
})
export class CommentModule {}
