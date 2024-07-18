import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseGuards,
  Req,
  UseInterceptors,
  UploadedFiles,
  Query,
} from '@nestjs/common';
import { PostService } from './post.service';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';
import { JwtAuthGuard } from '@/resources/auth/passport/guards';
import { FilesInterceptor } from '@nestjs/platform-express';
import { MediaService } from '@/resources/media/media.service';
import {
  ApiBody,
  ApiConsumes,
  ApiOperation,
  ApiParam,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { PostDto } from '@/resources/post/dto/post.dto';
import { MediaDto } from '@/resources/media/dto/media.dto';
import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { getPaginationSchema } from '@/infrastructure/swagger/helpers';
import { ReactToPostDto } from '@/resources/post/dto/react-to-post.dto';

// @UseGuards(JwtAuthGuard)
@ApiTags('posts')
@Controller('posts')
export class PostController {
  constructor(private readonly postService: PostService) {}

  @Post()
  @ApiBody({ type: CreatePostDto })
  @ApiOperation({ operationId: 'create-post' })
  @ApiResponse({ type: PostDto })
  create(@Body() createPostDto: CreatePostDto) {
    return this.postService.create(createPostDto, 1);
  }

  @Get()
  @ApiOperation({ operationId: 'get-posts' })
  @ApiResponse({
    status: 200,
    schema: getPaginationSchema(PostDto),
  })
  findAll(@Query() paginationQuery: PaginationQueryDto) {
    return this.postService.findAll(paginationQuery);
  }

  @Get('my')
  @ApiOperation({ operationId: 'get-my-posts' })
  @ApiResponse({
    status: 200,
    schema: getPaginationSchema(PostDto),
  })
  getMyPosts(@Req() req, @Query() paginationQuery: PaginationQueryDto) {
    return this.postService.getMyPosts(1, paginationQuery);
  }

  @Get(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'get-post' })
  @ApiResponse({ type: PostDto })
  findOne(@Param('id') id: string) {
    return this.postService.findOne(+id);
  }

  @Patch(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'update-post' })
  @ApiResponse({ type: PostDto })
  update(@Param('id') id: string, @Body() updatePostDto: UpdatePostDto) {
    return this.postService.update(+id, updatePostDto);
  }

  @Delete(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'delete-post' })
  @ApiResponse({ status: 200 })
  remove(@Param('id') id: string) {
    return this.postService.remove(+id);
  }

  @Post(':id/react')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'react-to-post' })
  @ApiResponse({ type: PostDto })
  @ApiBody({
    type: ReactToPostDto,
  })
  react(@Param('id') id: string, @Body() body: ReactToPostDto, @Req() req) {
    return this.postService.react(+id, 1, body.reaction);
  }

  @Post(':id/media')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiBody({
    type: MediaDto,
  })
  @ApiOperation({ operationId: 'upload-post-media' })
  @ApiResponse({ type: PostDto })
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: 1024 * 1024 * 50 }, // 50MB
      storage: MediaService.getStorage(),
    }),
  )
  addMedia(
    @Param('id') id: string,
    @UploadedFiles() files: Array<Express.Multer.File>,
  ) {
    return this.postService.addMedia(+id, files);
  }
}
