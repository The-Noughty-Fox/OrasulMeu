import { Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AuthService {
  constructor(
    private jwtService: JwtService,
    private configService: ConfigService,
  ) {}

  async signTokens(userPayload: any) {
    return this.jwtService.signAsync(userPayload, {
      secret: this.configService.get<string>('ACCESS_SECRET'),
      expiresIn: '7d',
    });
  }
}
