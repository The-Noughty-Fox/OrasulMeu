import { applyDecorators } from '@nestjs/common';
import { ApiBody, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { UserDto } from '../../user/dto/user.dto';
import { ApiBodyWithToken } from '../types';

export function AuthWithFacebookSwagger() {
  return applyDecorators(
    ApiOperation({
      operationId: 'auth-with-facebook',
      tags: ['auth'],
    }),
    ApiBody({ type: ApiBodyWithToken }),
    ApiResponse({
      status: 200,
      type: UserDto,
    }),
    ApiResponse({
      status: 401,
    }),
  );
}
