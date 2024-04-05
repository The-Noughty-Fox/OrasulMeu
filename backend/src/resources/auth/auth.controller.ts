import {
  Controller,
  NotFoundException,
  Param,
  ParseIntPipe,
  Post,
  Req,
  Res,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { ApiOperation, ApiParam, ApiResponse, ApiTags } from '@nestjs/swagger';
import { UserService } from '../user/user.service';
import { AuthService } from './auth.service';
import { UserDto } from '../user/dto/user.dto';
import {
  AppleAuthGuard,
  FacebookAuthGuard,
  GoogleAuthGuard,
} from './passport/guards';
import { AuthWithFacebookSwagger } from './swagger/authWithFacebook.decorator';
import { AuthWithGoogleSwagger } from './swagger/authWithGoogle.decorator';
import { AuthWithAppleSwagger } from './swagger/authWithApple.decorator';

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
    @Res({ passthrough: true }) res,
    @Param('id', ParseIntPipe) id: number,
  ): Promise<UserDto> {
    const user = await this.userService.findOne(id);
    if (!user) {
      throw new NotFoundException(`User with id ${id} does not exist`);
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
  @AuthWithAppleSwagger()
  async authenticateWithApple(
    @Req() req,
    @Res({ passthrough: true }) res,
  ): Promise<UserDto> {
    if (!req.user) {
      throw new UnauthorizedException('Apple authentication failed');
    }

    const user = req.user as UserDto;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return req.user;
  }

  @UseGuards(GoogleAuthGuard)
  @Post('google')
  @AuthWithGoogleSwagger()
  async authenticateWithGoogle(
    @Req() req,
    @Res({ passthrough: true }) res,
  ): Promise<UserDto> {
    if (!req.user) {
      throw new UnauthorizedException('Google authentication failed');
    }

    const user = req.user as UserDto;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return req.user;
  }

  @UseGuards(FacebookAuthGuard)
  @Post('facebook')
  @AuthWithFacebookSwagger()
  async authenticateWithFacebook(
    @Req() req,
    @Res({ passthrough: true }) res,
  ): Promise<UserDto> {
    if (!req.user) {
      throw new UnauthorizedException('Facebook authentication failed');
    }

    const user = req.user as UserDto;

    const accessToken = await this.authService.signTokens({
      id: user.id,
    });

    res.cookie('access_token', accessToken, {
      httpOnly: true,
      sameSite: 'strict',
    });

    return user;
  }
}
