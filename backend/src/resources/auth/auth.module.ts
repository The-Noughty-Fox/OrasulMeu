import { MiddlewareConsumer, Module, NestMiddleware } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { JwtModule } from '@nestjs/jwt';
import { UserModule } from '../user/user.module';
import { PassportModule } from '@nestjs/passport';
import { JwtStrategy } from './passport/strategies/jwt.strategy';
import { AppleStrategy } from './passport/strategies/apple.strategy';
import { GoogleStrategy } from './passport/strategies/google.strategy';
import { NextFunction } from 'express';
import { FacebookStrategy } from './passport/strategies/facebook.strategy';

class SIWAMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    const body = req.body as any;
    body.code = body.authorizationCode;
    next();
  }
}

@Module({
  controllers: [AuthController],
  imports: [PassportModule, JwtModule, UserModule],
  providers: [
    AuthService,
    JwtStrategy,
    AppleStrategy,
    GoogleStrategy,
    FacebookStrategy,
  ],
})
export class AuthModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(SIWAMiddleware).forRoutes('auth/apple');
  }
}
