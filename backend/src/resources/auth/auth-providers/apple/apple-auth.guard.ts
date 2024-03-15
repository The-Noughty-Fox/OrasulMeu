import { ExecutionContext, Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class AppleAuthenticationGuard extends AuthGuard('apple') {}

@Injectable()
export class AnonymousAppleAuthGuard extends AuthGuard('cookies') {
  constructor(private readonly appleGuard: AppleAuthenticationGuard) {
    super();
  }

  async canActivate(context: ExecutionContext): Promise<boolean> {
    return this.appleGuard.canActivate(context) as unknown as Promise<boolean>;
  }
}
