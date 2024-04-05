import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
} from '@nestjs/common';
import { CommentService } from './comment.service';
import { CreateCommentDto } from './dto/create-comment.dto';
import { UpdateCommentDto } from './dto/update-comment.dto';
import { ApiOperation, ApiTags } from '@nestjs/swagger';

@ApiTags('comments')
@Controller('comments')
export class CommentController {
  constructor(private readonly commentService: CommentService) {}

  @Post()
  @ApiOperation({ operationId: 'create-comment' })
  create(@Body() createCommentDto: CreateCommentDto) {
    return this.commentService.create(createCommentDto);
  }

  @Get()
  @ApiOperation({ operationId: 'get-comments' })
  findAll() {
    return this.commentService.findAll();
  }

  @Get(':id')
  @ApiOperation({ operationId: 'get-comment' })
  findOne(@Param('id') id: string) {
    return this.commentService.findOne(+id);
  }

  @Patch(':id')
  @ApiOperation({ operationId: 'update-comment' })
  update(@Param('id') id: string, @Body() updateCommentDto: UpdateCommentDto) {
    return this.commentService.update(+id, updateCommentDto);
  }

  @Delete(':id')
  @ApiOperation({ operationId: 'delete-comment' })
  remove(@Param('id') id: string) {
    return this.commentService.remove(+id);
  }
}
