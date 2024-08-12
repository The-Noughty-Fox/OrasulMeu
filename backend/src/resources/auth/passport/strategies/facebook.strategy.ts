import { PassportStrategy } from '@nestjs/passport';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UserService } from '../../../user/user.service';
import { Request } from 'express';
import { SocialMedia } from '@/shared/types';
import axios from 'axios';
import { FACEBOOK_GRAPH_ME_API_URL } from '@/app-config/facebook/constants';
import { Strategy } from 'passport-custom';

@Injectable()
export class FacebookStrategy extends PassportStrategy(Strategy, 'facebook') {
  constructor(private readonly userService: UserService) {
    super();
  }

  async validate(req: Request) {
    const { token } = req.body;
    if (!token) throw new UnauthorizedException();

    const { data } = await axios.get<any>(
      FACEBOOK_GRAPH_ME_API_URL.replace('@', token),
    );

    const existingUser =
      (await this.userService.findBySocialMediaToken(
        SocialMedia.Facebook,
        data.id as string,
      )) || (await this.userService.findByEmail(data.email));

    if (!existingUser) {
      await this.userService.create({
        email: data.email || 'Unknown',
        username: `${data.first_name || 'Unknown'} ${data.last_name || 'Unknown'}`,
        facebookToken: data.id as string,
        socialProfilePictureUrl: data.picture.data.url,
      });
    }

    return existingUser;
  }
}
