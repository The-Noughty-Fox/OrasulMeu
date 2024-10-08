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
  ParseIntPipe,
} from '@nestjs/common';
import { PostService } from './post.service';
import { CreatePostDto } from './dto/create-post.dto';
import { UpdatePostDto } from './dto/update-post.dto';
import { JwtAuthGuard } from '@/resources/auth/passport/guards';
import { FilesInterceptor } from '@nestjs/platform-express';
import {
  ApiBody,
  ApiConsumes,
  ApiOperation,
  ApiParam,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { PostDto } from '@/resources/post/dto/post.dto';
import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { getPaginationSchema } from '@/infrastructure/swagger/helpers';
import { ReactToPostDto } from '@/resources/post/dto/react-to-post.dto';
import { mbToBytes } from '@/helpers/other';
import { PostsByPhraseQueryDto } from './dto/posts-by-phrase-query.dto';
import { MediaDto } from '@/resources/media/dto/media.dto';

@ApiTags('posts')
@Controller('posts')
export class PostController {
  constructor(private readonly postService: PostService) {}

  // ========================================================================
  // =                controllers for anonymous users                       =
  // ========================================================================

  @Get('anonymous')
  @ApiOperation({ operationId: 'get-all-posts-anonymous' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findAllForAnonymousUser(@Query() paginationQuery: PaginationQueryDto) {
    return this.postService.findAllForAnonymousUser(paginationQuery);
  }

  @Get('search/anonymous')
  @ApiOperation({ operationId: 'get-posts-by-phrase-anonymous' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findByPhraseForAnonymousUser(@Query() query: PostsByPhraseQueryDto) {
    return this.postService.findByPhraseForAnonymousUser(query);
  }

  @Get('reaction/anonymous')
  @ApiOperation({
    operationId: 'get-all-posts-ordered-by-reactions-count-anonymous',
  })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findAllReactionCountOrderForAnonymousUser(
    @Query() paginationQuery: PaginationQueryDto,
  ) {
    return this.postService.findAllReactionCountOrderForAnonymousUser(
      paginationQuery,
    );
  }

  @Get(':id/anonymous')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'get-post-anonymous' })
  @ApiResponse({ type: PostDto })
  findOneForAnonymousUser(@Param('id', ParseIntPipe) id: number) {
    return this.postService.findOneForAnonymousUser(id);
  }

  // ========================================================================
  // =                    controllers for loged users                       =
  // ========================================================================

  @UseGuards(JwtAuthGuard)
  @Post()
  @ApiBody({ type: CreatePostDto })
  @ApiOperation({ operationId: 'create-post' })
  @ApiResponse({
    description: 'Post created successfully',
    type: PostDto,
  })
  create(@Body() createPostDto: CreatePostDto, @Req() req) {
    return this.postService.create(createPostDto, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  @ApiOperation({ operationId: 'get-all-posts' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findAll(@Query() paginationQuery: PaginationQueryDto, @Req() req) {
    console.log('in here');
    return this.postService.findAll(paginationQuery, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Get('search')
  @ApiOperation({ operationId: 'get-posts-by-phrase' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findByPhrase(@Query() query: PostsByPhraseQueryDto, @Req() req) {
    return this.postService.findByPhrase(query, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Get('reaction')
  @ApiOperation({ operationId: 'get-all-posts-ordered-by-reactions-count' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  findAllReactionCountOrder(
    @Query() paginationQuery: PaginationQueryDto,
    @Req() req,
  ) {
    return this.postService.findAllReactionCountOrder(
      paginationQuery,
      req.user.id,
    );
  }

  @UseGuards(JwtAuthGuard)
  @Get('my')
  @ApiOperation({ operationId: 'get-my-posts' })
  @ApiResponse({
    schema: getPaginationSchema(PostDto),
  })
  getMyPosts(@Req() req, @Query() paginationQuery: PaginationQueryDto) {
    return this.postService.findMyPosts(req.user.id, paginationQuery);
  }

  @UseGuards(JwtAuthGuard)
  @Get(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'get-post' })
  @ApiResponse({ type: PostDto })
  findOne(@Param('id', ParseIntPipe) id: number, @Req() req) {
    return this.postService.findOne(id, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Patch(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiBody({ type: UpdatePostDto })
  @ApiOperation({ operationId: 'update-post' })
  @ApiResponse({ type: PostDto })
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() updatePostDto: UpdatePostDto,
    @Req() req,
  ) {
    return this.postService.update(id, updatePostDto, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'delete-post' })
  @ApiResponse({ status: 200 })
  remove(@Param('id', ParseIntPipe) id: number, @Req() req) {
    return this.postService.remove(id, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/react')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'react-to-post' })
  @ApiResponse({ type: PostDto })
  @ApiBody({
    type: ReactToPostDto,
  })
  react(
    @Param('id', ParseIntPipe) id: number,
    @Body() body: ReactToPostDto,
    @Req() req,
  ) {
    return this.postService.react(id, req.user.id, body.reaction);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id/react')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'retrieve-reaction-to-post' })
  @ApiResponse({ type: PostDto })
  retrieveReaction(@Param('id', ParseIntPipe) id: number, @Req() req) {
    return this.postService.retrieveReaction(id, req.user.id);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/media')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiBody({
    type: MediaDto,
  })
  @ApiOperation({ operationId: 'upload-post-media' })
  @ApiBody({
    description: 'File data',
    type: MediaDto,
  })
  @ApiResponse({ type: PostDto })
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: mbToBytes(50) },
    }),
  )
  addMedia(
    @Param('id', ParseIntPipe) id: number,
    @Req() req,
    @UploadedFiles() files: Array<Express.Multer.File>,
  ) {
    return this.postService.addMedia(id, req.user.id, files);
  }
}
