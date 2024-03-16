import {
  Controller,
  Param,
  ParseIntPipe,
  Post,
  Req,
  Res,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBody,
  ApiOperation,
  ApiParam,
  ApiProperty,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { UserService } from '../user/user.service';
import { AuthService } from './auth.service';
import { UserDto } from '../user/dto/user.dto';
import { GoogleAuthGuard } from './auth-providers/google/google-auth.guard';
import { AppleAuthGuard } from './auth-providers/apple/apple-auth.guard';

class token {
  @ApiProperty()
  token: string;
}

class appleToken {
  @ApiProperty()
  authorizationCode: string;
}

@Controller('auth')
@ApiTags('auth')
export class AuthController {
  constructor(
    private readonly userService: UserService,
    private readonly authService: AuthService,
  ) {}

  @Post('test/:id')
  @ApiParam({ name: 'id', type: 'integer', required: true })
  @ApiOperation({ operationId: 'login-test-user' })
  @ApiResponse({
    status: 201,
    description: 'The user has been successfully authenticated.',
    type: UserDto,
  })
  async loginTest(
    @Req() req,
    @Res({ passthrough: true }) res,
    @Param('id', ParseIntPipe) id: number,
  ): Promise<UserDto> {
    // Potentially stupid small hack to let us debug easier
    const user = await this.userService.findOne(id);
    if (!user) {
      throw new UnauthorizedException(`User with id ${id} does not exist`);
    }

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return user;
  }

  @UseGuards(AppleAuthGuard)
  @Post('apple')
  @ApiBody({ type: appleToken })
  @ApiResponse({
    status: 201,
    description: 'The user has been successfully authenticated.',
    type: UserDto,
  })
  async authenticateApple(@Req() req, @Res() res): Promise<UserDto> {
    const user = req.user;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return req.user;

    return Promise.reject('Apple authentication failed');
  }

  @UseGuards(GoogleAuthGuard)
  @Post('google')
  @ApiBody({ type: token })
  @ApiResponse({
    status: 201,
    description: 'The user has been successfully authenticated.',
    type: UserDto,
  })
  async authenticateGoogle(
    @Req() req,
    @Res({ passthrough: true }) res,
  ): Promise<UserDto> {
    const user = req.user;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return req.user;

    throw new UnauthorizedException('Google authorization failed.');
  }

  @Post('facebook')
  @ApiBody({ type: token })
  @ApiResponse({
    status: 201,
    description: 'The user has been successfully authenticated.',
    type: UserDto,
  })
  async authenticateWithFacebookPo(
    @Req() req,
    @Res({ passthrough: true }) res,
  ): Promise<UserDto> {
    const user = req.user;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return req.user;

    return Promise.reject('Facebook authentication failed');
  }
}
