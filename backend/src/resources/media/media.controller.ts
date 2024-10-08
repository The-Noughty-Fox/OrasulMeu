import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  UploadedFiles,
  UseGuards,
  ParseIntPipe,
  Param,
  Delete,
} from '@nestjs/common';
import { MediaService } from './media.service';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import {
  ApiBody,
  ApiConsumes,
  ApiOperation,
  ApiParam,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { mbToBytes } from '@/helpers/other';
import { JwtAuthGuard } from '../auth/passport/guards';
import { MediaSupabaseDto } from './dto/media-supabase.dto';
import { MediaDto, MediaSingleDto } from '@/resources/media/dto/media.dto';

@UseGuards(JwtAuthGuard)
@ApiTags('media')
@Controller('media')
export class MediaController {
  constructor(private readonly mediaService: MediaService) {}

  @Post()
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    description: 'File data',
    type: MediaSingleDto,
  })
  @ApiResponse({ type: MediaSupabaseDto })
  @UseInterceptors(
    FileInterceptor('file', {
      limits: { fileSize: mbToBytes(50) },
    }),
  )
  async upload(@UploadedFile() file: Express.Multer.File) {
    const [newFile] = await this.mediaService.create([file]);

    return newFile;
  }

  @Delete(':id')
  @ApiParam({ name: 'id', type: 'integer' })
  @ApiOperation({ operationId: 'delete-media' })
  @ApiResponse({ status: 200 })
  deleteFile(@Param('id', ParseIntPipe) id: number) {
    return this.mediaService.deleteFile(id);
  }

  @Post('files')
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    description: 'File data',
    type: MediaDto,
  })
  @ApiResponse({ type: MediaSupabaseDto, isArray: true })
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: mbToBytes(50) },
    }),
  )
  uploadFiles(@UploadedFiles() files: Express.Multer.File[]) {
    return this.mediaService.create(files);
  }
}
