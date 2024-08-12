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
  @ApiParam({ name: 'id', description: 'User id', type: 'number' })
  @ApiResponse({
    description: 'User profile found',
    type: UserProfileDto,
  })
  getProfile(@Param('id', ParseIntPipe) id: number) {
    return this.userService.profile(id);
  }

  @Get(':id')
  @ApiResponse({
    description: 'User found',
    type: UserDto,
  })
  @ApiParam({ name: 'id', description: 'User id', type: 'number' })
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.userService.findOne(id);
  }

  @Patch()
  @ApiResponse({
    description: 'User updated',
    type: UserDto,
  })
  update(@Req() req, @Body() updateUserDto: UserUpdateDto) {
    return this.userService.update(req.user.id, updateUserDto);
  }

  // NOT IMPLEMENTED YET
  // @Delete(':id')
  // remove(@Param('id') id: string) {}

  // @Get('search')
  // search() {
  //   return this.userService.findAll();
  // }
}
