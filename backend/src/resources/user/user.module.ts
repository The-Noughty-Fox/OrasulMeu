import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { UserProfile } from './user.profile';
import { SupabaseModule } from '../supabase/supabase.module';

@Module({
  imports: [TypeOrmModule.forFeature([User]), SupabaseModule],
  controllers: [UserController],
  providers: [UserService, UserProfile],
  exports: [UserService, UserProfile],
})
export class UserModule {}
