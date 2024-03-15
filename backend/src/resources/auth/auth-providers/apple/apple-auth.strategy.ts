import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import * as AppleStrategy from '@nicokaiser/passport-apple';
import { UserService } from '../../../user/user.service';
import { SocialMedia } from '../../../../shared/types';
import { UserDto } from '../../../user/dto/user.dto';
import * as fs from 'fs';

@Injectable()
export class AppleAuthStrategy extends PassportStrategy(
  AppleStrategy.Strategy,
) {
  constructor(private readonly userService: UserService) {
    super({
      clientID: process.env.APPLE_CLIENT_ID,
      teamID: process.env.APPLE_TEAM_ID,
      keyID: process.env.APPLE_KEY_ID,
      key: fs.readFileSync('./apple_config/AuthKey.p8'),
      passReqToCallback: true,
    });
    console.log({
      clientID: process.env.APPLE_CLIENT_ID,
      teamID: process.env.APPLE_TEAM_ID,
      keyID: process.env.APPLE_KEY_ID,
    });
  }

  async validate(
    req: any,
    accessToken: string,
    refreshToken: string,
    profile: any,
  ): Promise<UserDto> {
    const { id, email } = profile;

    if (!id)
      return Promise.reject(
        "Apple jwt token does not contain the 'sub' field.",
      );

    const existingUser =
      (await this.userService.findBySocialMediaToken(
        SocialMedia.Apple,
        id as string,
      )) ||
      (await this.userService.findByEmail(email)) ||
      (await this.userService.create({
        email,
        firstName: (req.body as any)?.userInfo?.Name.given,
        lastName: (req.body as any)?.userInfo?.Name.family,
        apple_token: id,
      }));

    return existingUser;
  }
}
