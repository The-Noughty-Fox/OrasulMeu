import { Module, Global } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { AppleConfig } from './apple/apple.config';
import { GoogleConfig } from './google/google.config';
import * as fs from 'fs';
import { GOOGLE_AUDIENCE, GOOGLE_CLIENT_ID } from './google/constants';

@Global()
@Module({
  imports: [ConfigModule],
  providers: [
    {
      provide: 'APPLE_CONFIG',
      useFactory: (configService: ConfigService): AppleConfig => {
        const clientID = configService.get<string>('APPLE_CLIENT_ID');
        const teamID = configService.get<string>('APPLE_TEAM_ID');
        const keyID = configService.get<string>('APPLE_KEY_ID');
        const key = fs.readFileSync('./apple_config/AuthKey.p8');
        const scope = configService.get<string[]>('APPLE_SCOPE');

        return new AppleConfig(clientID, teamID, keyID, key, scope);
      },
      inject: [ConfigService],
    },
    {
      provide: 'GOOGLE_CONFIG',
      useFactory: (configService: ConfigService): GoogleConfig => {
        const clientID = GOOGLE_CLIENT_ID;
        const clientSecret = configService.get<string>('GOOGLE_CLIENT_SECRET');
        const audience = GOOGLE_AUDIENCE;
        const scope = ['name', 'email'];

        return new GoogleConfig(clientID, clientSecret, audience, scope);
      },
      inject: [ConfigService],
    },
  ],
  exports: ['APPLE_CONFIG', 'GOOGLE_CONFIG'],
})
export class AppConfigModule {}
