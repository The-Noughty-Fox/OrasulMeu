import { Module, Global } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { AppleConfig } from './apple/apple.config';
import { GoogleConfig } from './google/google.config';
import * as fs from 'fs';
import {
  GOOGLE_AUDIENCE,
  GOOGLE_CLIENT_ID,
  GOOGLE_SCOPE,
} from './google/constants';
import {
  APPLE_CLIENT_ID,
  APPLE_KEY_ID,
  APPLE_SCOPE,
  APPLE_TEAM_ID,
} from './apple/constants';
import { FacebookConfig } from '@/app-config/facebook/facebook.config';
import { FACEBOOK_CLIENT_ID } from '@/app-config/facebook/constants';
import * as path from 'path';

@Global()
@Module({
  imports: [ConfigModule],
  providers: [
    {
      provide: 'APPLE_CONFIG',
      useFactory: (): AppleConfig => {
        const key = fs.readFileSync(
          path.join(__dirname, 'files/AuthKey_9555S3QMPW.p8'),
        );

        return new AppleConfig(
          APPLE_CLIENT_ID,
          APPLE_TEAM_ID,
          APPLE_KEY_ID,
          key,
          APPLE_SCOPE,
        );
      },
      inject: [ConfigService],
    },
    {
      provide: 'GOOGLE_CONFIG',
      useFactory: (configService: ConfigService): GoogleConfig => {
        const clientSecret = configService.get<string>('GOOGLE_CLIENT_SECRET');

        return new GoogleConfig(
          GOOGLE_CLIENT_ID,
          clientSecret,
          GOOGLE_AUDIENCE,
          GOOGLE_SCOPE,
        );
      },
      inject: [ConfigService],
    },
    {
      provide: 'FACEBOOK_CONFIG',
      useFactory: (configService: ConfigService): FacebookConfig => {
        const clientSecret = configService.get<string>(
          'FACEBOOK_CLIENT_SECRET',
        );

        return new FacebookConfig(FACEBOOK_CLIENT_ID, clientSecret);
      },
      inject: [ConfigService],
    },
  ],
  exports: ['APPLE_CONFIG', 'GOOGLE_CONFIG'],
})
export class AppConfigModule {}
