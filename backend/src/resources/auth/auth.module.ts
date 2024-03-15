import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { AppleAuthStrategy } from './auth-providers/apple/apple-auth.strategy';
import { AnonymousAppleAuthGuard } from './auth-providers/apple/apple-auth.guard';
import { JwtStrategy } from './passport/strategies/jwt.strategy';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { UserModule } from '../user/user.module';

@Module({
  controllers: [AuthController],
  imports: [PassportModule, JwtModule, UserModule],
  providers: [
    AuthService,
    AppleAuthStrategy,
    AnonymousAppleAuthGuard,
    JwtStrategy,
  ],
  exports: [AuthService],
})
export class AuthModule {}
