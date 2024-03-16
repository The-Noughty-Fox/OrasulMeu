import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { JwtModule } from '@nestjs/jwt';
import { UserModule } from '../user/user.module';
import { PassportModule } from '@nestjs/passport';
import { JwtStrategy } from './passport/strategies/jwt.strategy';
import { AppleStrategy } from './passport/strategies/apple.strategy';

@Module({
  controllers: [AuthController],
  imports: [PassportModule, JwtModule, UserModule],
  providers: [AuthService, JwtStrategy, AppleStrategy],
})
export class AuthModule {}
