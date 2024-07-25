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
  ApiBadRequestResponse,
  ApiConflictResponse,
  ApiCreatedResponse,
  ApiInternalServerErrorResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
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
  @ApiCreatedResponse({
    description: 'User successfully created',
    type: UserDto,
  })
  @ApiBadRequestResponse({ description: 'Bad Request. No token provided' })
  @ApiConflictResponse({ description: 'Conflict. Invalid input data' })
  @ApiInternalServerErrorResponse({ description: 'Internal Server Error' })
  create(@Body() createUserDto: UserCreateDto) {
    return this.userService.create(createUserDto);
  }

  @Get()
  @ApiOkResponse({
    description: 'Users found',
    type: UserDto,
    isArray: true,
  })
  @ApiInternalServerErrorResponse({ description: 'Internal Server Error' })
  findAll() {
    return this.userService.findAll();
  }

  @Get('profile/:id')
  @ApiOperation({ operationId: 'get-user-profile' })
  @ApiParam({ name: 'id', description: 'User id', type: 'number' })
  @ApiOkResponse({
    description: 'User profile found',
    type: UserProfileDto,
  })
  @ApiNotFoundResponse({ description: 'User profile not found' })
  getProfile(@Param('id', ParseIntPipe) id: number) {
    return this.userService.profile(id);
  }

  @Get(':id')
  @ApiOkResponse({
    description: 'User found',
    type: UserDto,
  })
  @ApiParam({ name: 'id', description: 'User id', type: 'number' })
  @ApiNotFoundResponse({ description: 'User not found' })
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.userService.findOne(id);
  }

  @Patch()
  @ApiOkResponse({
    description: 'User updated',
    type: UserDto,
  })
  @ApiInternalServerErrorResponse({ description: 'Internal Server Error' })
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
