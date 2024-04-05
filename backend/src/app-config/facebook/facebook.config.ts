import { Injectable } from '@nestjs/common';

@Injectable()
export class FacebookConfig {
  constructor(
    public clientID: string,
    public clientSecret: string,
  ) {}
}
