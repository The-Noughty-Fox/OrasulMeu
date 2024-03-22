import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class EchoService {
  constructor(private configService: ConfigService) {}

  echo(): string {
    return this.configService.get<string>('GOOGLE_CLIENT_ID');
  }
}
