import { Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class AppleAuthGuard extends AuthGuard('apple') {}

@Injectable()
export class GoogleAuthGuard extends AuthGuard('google') {}

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {}

@Injectable()
export class FacebookAuthGuard extends AuthGuard('facebook') {}
