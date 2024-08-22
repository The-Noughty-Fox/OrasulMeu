import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Req,
  UseGuards,
  ParseIntPipe,
  Delete,
  Res,
} from '@nestjs/common';
import { UserService } from './user.service';
import { UserDto } from './dto/user.dto';
import {
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { JwtAuthGuard } from '@/resources/auth/passport/guards';
import { UserProfileDto } from '@/resources/user/dto/user-profile.dto';
import { UserCreateDto } from './dto/user-create.dto';
import { UserUpdateDto } from './dto/user-update.dto';

@UseGuards(JwtAuthGuard)
@Controller('users')
@ApiTags('users')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Post()
  @ApiResponse({
    description: 'User successfully created',
    type: UserDto,
  })
  create(@Body() createUserDto: UserCreateDto) {
    return this.userService.create(createUserDto);
  }

  @Get()
  @ApiOkResponse({
    description: 'Users found',
    type: UserDto,
    isArray: true,
  })
  findAll() {
    return this.userService.findAll();
  }

  @Get('profile/:id')
  @ApiOperation({ operationId: 'get-user-profile' })
  @ApiParam({ name: 'id', description: 'User id', type: 'integer' })
  @ApiResponse({
    description: 'User profile found',
    type: UserProfileDto,
  })
  getProfile(@Param('id', ParseIntPipe) id: number) {
    return this.userService.profile(id);
  }

  @Get(':id')
  @ApiOperation({ operationId: 'find-user-by-id' })
  @ApiResponse({
    description: 'User found',
    type: UserDto,
  })
  @ApiParam({ name: 'id', description: 'User id', type: 'integer' })
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.userService.findOne(id);
  }

  @Patch()
  @ApiOperation({ operationId: 'edit-user' })
  @ApiResponse({
    description: 'User updated',
    type: UserDto,
  })
  update(@Req() req, @Body() updateUserDto: UserUpdateDto) {
    return this.userService.update(req.user.id, updateUserDto);
  }

  @Delete()
  @ApiOperation({ operationId: 'delete-user' })
  @ApiResponse({ status: 200 })
  async remove(@Req() req, @Res() res) {
    const result = await this.userService.remove(req.user.id);

    if (result) {
      res.cookie('access_token', '', {
        httpOnly: true,
        sameSite: 'strict',
        maxAge: 0,
      });
    }

    return res.status(200).send(result);
  }

  // NOT IMPLEMENTED YET
  // @Get('search')
  // search() {
  //   return this.userService.findAll();
  // }
}
