import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import axios from 'axios';
import { UserService } from '../../../user/user.service';
import { SocialMedia } from '../../../../shared/types';

@Injectable()
export class FacebookAuthGuard implements CanActivate {
  constructor(private readonly userService: UserService) {}
  async canActivate(context: ExecutionContext): Promise<boolean> {
    const req = context.switchToHttp().getRequest();
    const { token } = req.body;
    if (!token)
      return Promise.reject(
        `Facebook authentication requires 'token' be sent in body`,
      );

    const { data } =
      await axios.get<any>(`https://graph.facebook.com/me?access_token=${token}&
                fields=id,name,email,first_name,last_name,gender,picture`);

    req.user =
      (await this.userService.findBySocialMediaToken(
        SocialMedia.Facebook,
        data.id as string,
      )) ||
      (await this.userService.findByEmail(data.email)) ||
      (await this.userService.create({
        email: data.email || 'Unknown',
        firstName: data.first_name || 'Unknown',
        lastName: data.last_name || 'Unknown',
        facebook_token: data.id as string,
        socialProfilePictureUrl: data.picture.data.url,
      }));

    return true;
  }
}
