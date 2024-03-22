import { Inject, Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import * as PassportAppleStrategy from '@nicokaiser/passport-apple';
import { UserService } from '../../../user/user.service';
import { UserDto } from '../../../user/dto/user.dto';
import { SocialMedia } from '../../../../shared/types';
import { AppleConfig } from '../../../../app-config/apple/apple.config';

@Injectable()
export class AppleStrategy extends PassportStrategy(
  PassportAppleStrategy.Strategy,
  'apple',
) {
  constructor(
    private readonly userService: UserService,
    @Inject('APPLE_CONFIG') private appleConfig: AppleConfig,
  ) {
    super({
      ...appleConfig,
      passReqToCallback: true,
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
