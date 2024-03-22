import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { JwtModule } from '@nestjs/jwt';
import { UserModule } from '../user/user.module';
import { PassportModule } from '@nestjs/passport';
import { JwtStrategy } from './passport/strategies/jwt.strategy';
import { AppleStrategy } from './passport/strategies/apple.strategy';
import { GoogleStrategy } from './passport/strategies/google.strategy';
import { AppConfigModule } from '../../app-config/app-config.module';

@Module({
  controllers: [AuthController],
  imports: [PassportModule, JwtModule, UserModule],
  providers: [AuthService, JwtStrategy, AppleStrategy, GoogleStrategy],
})
export class AuthModule {}
