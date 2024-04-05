import { ApiBody, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { applyDecorators } from '@nestjs/common';
import { ApiBodyWithToken } from '../types';
import { UserDto } from '../../user/dto/user.dto';

export function AuthWithGoogleSwagger() {
  return applyDecorators(
    ApiOperation({
      operationId: 'auth-with-google',
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
