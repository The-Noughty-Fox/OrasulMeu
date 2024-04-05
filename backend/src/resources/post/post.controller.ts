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
} from '@nestjs/common';
import { PostService } from './post.service';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';
import { JwtAuthGuard } from '@/resources/auth/passport/guards';
import { FilesInterceptor } from '@nestjs/platform-express';
import { MediaService } from '@/resources/media/media.service';
import {
  ApiBody,
  ApiOperation,
  ApiParam,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { PostDto } from '@/resources/post/dto/post.dto';

@UseGuards(JwtAuthGuard)
@ApiTags('posts')
@Controller('posts')
export class PostController {
  constructor(private readonly postService: PostService) {}

  @Post()
  @ApiBody({ type: CreatePostDto })
  @ApiOperation({ operationId: 'create-post' })
  @ApiResponse({ type: PostDto })
  create(@Body() createPostDto: CreatePostDto, @Req() req) {
    return this.postService.create(createPostDto, req.user.id);
  }

  @Get()
  @ApiOperation({ operationId: 'get-posts' })
  @ApiResponse({ type: PostDto, isArray: true })
  findAll() {
    return this.postService.findAll();
  }

  @Get(':id')
  @ApiParam({ name: 'id', type: Number })
  @ApiOperation({ operationId: 'get-post' })
  @ApiResponse({ type: PostDto })
  findOne(@Param('id') id: string) {
    return this.postService.findOne(+id);
  }

  @Patch(':id')
  @ApiParam({ name: 'id', type: Number })
  @ApiOperation({ operationId: 'update-post' })
  @ApiResponse({ type: PostDto })
  update(@Param('id') id: string, @Body() updatePostDto: UpdatePostDto) {
    return this.postService.update(+id, updatePostDto);
  }

  @Delete(':id')
  @ApiParam({ name: 'id', type: Number })
  @ApiOperation({ operationId: 'delete-post' })
  @ApiResponse({ status: 200 })
  remove(@Param('id') id: string) {
    return this.postService.remove(+id);
  }

  @Post(':id/like')
  @ApiParam({ name: 'id', type: Number })
  @ApiOperation({ operationId: 'like-post' })
  @ApiResponse({ status: 200 })
  like(@Param('id') id: string, @Req() req) {
    return this.postService.like(+id, req.user.id);
  }

  @Post(':id/dislike')
  @ApiParam({ name: 'id', type: Number })
  @ApiOperation({ operationId: 'dislike-post' })
  @ApiResponse({ status: 200 })
  dislike(@Param('id') id: string, @Req() req) {
    return this.postService.dislike(+id, req.user.id);
  }

  @Post(':id/media')
  @ApiParam({ name: 'id', type: Number })
  @ApiBody({
    type: 'Array of files',
    isArray: true,
    examples: { files: { value: 'file' } },
  })
  @ApiOperation({ operationId: 'upload-post-media' })
  @ApiResponse({ type: PostDto })
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: 1024 * 1024 * 50 }, // 50MB
      storage: MediaService.getStorage(),
    }),
  )
  addMedia(
    @Param('id') id: string,
    @UploadedFiles() files: Express.Multer.File[],
  ) {
    return this.postService.addMedia(+id, files);
  }
}
