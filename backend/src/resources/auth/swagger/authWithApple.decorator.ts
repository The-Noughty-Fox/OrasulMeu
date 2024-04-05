import { ApiBody, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { applyDecorators } from '@nestjs/common';
import { UserDto } from '../../user/dto/user.dto';
import { AppleApiBody } from '../types';

export function AuthWithAppleSwagger() {
  return applyDecorators(
    ApiOperation({
      operationId: 'auth-with-apple',
      tags: ['auth'],
    }),
    ApiBody({ type: AppleApiBody }),
    ApiResponse({
      status: 200,
      type: UserDto,
    }),
    ApiResponse({
      status: 401,
    }),
  );
}
