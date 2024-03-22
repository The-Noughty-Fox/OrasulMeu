import { Module } from '@nestjs/common';
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

@Module({
  imports: [
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
      entities: [User],
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
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
