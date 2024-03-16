import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { OAuth2Client } from 'google-auth-library';
import { ConfigService } from '@nestjs/config';
import { UserService } from '../../../user/user.service';
import { SocialMedia } from '../../../../shared/types';

@Injectable()
export class GoogleAuthGuard implements CanActivate {
  private readonly client: OAuth2Client;

  constructor(
    private readonly userService: UserService,
    private readonly configService: ConfigService,
  ) {
    this.client = new OAuth2Client(
      configService.get<string>('GOOGLE_CLIENT_ID'),
    );
  }

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const req = context.switchToHttp().getRequest();

    const { token } = req.body;
    if (!token)
      return Promise.reject(
        `Google authentication requires 'token' be sent in body`,
      );
    const ticket = await this.client.verifyIdToken({
      idToken: token,
      audience: this.configService.get<string>('GOODLE_AUDIENCE'),
    });
    const {
      given_name: name,
      sub: id,
      email,
      family_name: familyName,
      picture,
    } = ticket.getPayload();

    const existingUser =
      (await this.userService.findBySocialMediaToken(SocialMedia.Google, id)) ||
      (await this.userService.findByEmail(email));

    if (!existingUser) {
      req.user = await this.userService.create({
        email,
        firstName: name,
        lastName: familyName,
        socialProfilePictureUrl: picture,
        google_token: id,
      });

      return true;
    }
    req.user = existingUser;

    return true;
  }
}
