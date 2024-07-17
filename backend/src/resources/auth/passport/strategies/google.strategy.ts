import { Inject, Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy } from 'passport-custom';
import { Request } from 'express';
import { OAuth2Client } from 'google-auth-library';
import { SocialMedia } from '../../../../shared/types';
import { UserService } from '../../../user/user.service';
import { GoogleConfig } from '../../../../app-config/google/google.config';

@Injectable()
export class GoogleStrategy extends PassportStrategy(Strategy, 'google') {
  private readonly client: OAuth2Client;

  constructor(
    private readonly userService: UserService,
    @Inject('GOOGLE_CONFIG') private googleConfig: GoogleConfig,
  ) {
    super();
    this.client = new OAuth2Client(googleConfig.clientID);
  }

  async validate(req: Request) {
    const { token } = req.body;
    if (!token) throw new UnauthorizedException();

    const ticket = await this.client.verifyIdToken({
      idToken: token,
      audience: this.googleConfig.audience,
    });

    const { email, sub, given_name, family_name, picture } =
      await ticket.getPayload();

    const existingUser =
      (await this.userService.findBySocialMediaToken(
        SocialMedia.Google,
        sub,
      )) || (await this.userService.findByEmail(email));

    if (!existingUser) {
      return this.userService.create({
        email,
        firstName: given_name,
        lastName: family_name || '',
        socialProfilePictureUrl: picture,
        googleToken: sub,
      });
    }

    return existingUser;
  }
}
