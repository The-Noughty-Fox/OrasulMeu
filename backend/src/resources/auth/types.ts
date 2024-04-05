import { ApiProperty } from '@nestjs/swagger';

export class ApiBodyWithToken {
  @ApiProperty()
  token: string;
}

export class AppleApiBody {
  @ApiProperty()
  authorizationCode: string;
}
