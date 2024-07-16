import { MiddlewareConsumer, Module } from '@nestjs/common';
import { EchoModule } from './resources/echo/echo.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserModule } from './resources/user/user.module';
import { User } from './resources/user/entities/user.entity';
import { ConfigModule } from '@nestjs/config';
import { AutomapperModule } from '@automapper/nestjs';
import { classes } from '@automapper/classes';
import { AuthModule } from './resources/auth/auth.module';
import * as path from 'path';
import { CookieResolver, I18nModule } from 'nestjs-i18n';
import { AppConfigModule } from './app-config/app-config.module';
import { InfrastructureModule } from './infrastructure/infrastructure.module';
import { LoggingMiddleware } from './infrastructure/middleware/logging.middleware';
import { PostModule } from './resources/post/post.module';
import { Post } from '@/resources/post/entities/post.entity';
import { CommentModule } from './resources/comment/comment.module';
import { Comment } from '@/resources/comment/entities/comment.entity';
import { MediaModule } from './resources/media/media.module';
import { Media } from '@/resources/media/entities/media.entity';
import { ServeStaticModule } from '@nestjs/serve-static';
import { PostMedia } from '@/resources/media/entities/post-media.entity';
import { PostReaction } from '@/resources/post/entities/post-reaction.entity';
import { SupabaseModule } from '@/resources/supabase/supabase.module';

@Module({
  imports: [
    ServeStaticModule.forRoot({
      rootPath: path.join(__dirname, '..', 'uploads'),
      serveRoot: '/uploads',
    }),
    ConfigModule.forRoot({
      envFilePath: '.env',
      isGlobal: true,
    }),
    AutomapperModule.forRoot({
      strategyInitializer: classes(),
    }),
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: process.env.DB_HOST,
      port: parseInt(process.env.DB_PORT || '5432'),
      username: process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      entities: [User, Post, PostReaction, Comment, Media, PostMedia],
      synchronize: true,
      migrationsTableName: 'migrations',
    }),
    I18nModule.forRoot({
      fallbackLanguage: 'en',
      fallbacks: {
        en_US: 'en',
        da_DK: 'da',
      },
      loaderOptions: {
        path: path.join(__dirname, '/i18n/'),
        watch: true,
      },
      resolvers: [new CookieResolver()],
    }),
    AppConfigModule,
    EchoModule,
    UserModule,
    AuthModule,
    InfrastructureModule,
    PostModule,
    CommentModule,
    MediaModule,
    SupabaseModule,
  ],
})
export class AppModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggingMiddleware).forRoutes('*');
  }
}
