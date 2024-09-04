import { MiddlewareConsumer, Module } from '@nestjs/common';
import { EchoModule } from './resources/echo/echo.module';
import { UserModule } from './resources/user/user.module';
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
import { CommentModule } from './resources/comment/comment.module';
import { MediaModule } from './resources/media/media.module';
import { ServeStaticModule } from '@nestjs/serve-static';
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
