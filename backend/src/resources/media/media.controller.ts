import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  UploadedFiles,
  UseGuards,
  Delete,
  Body,
} from '@nestjs/common';
import { MediaService } from './media.service';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import {
  ApiBody,
  ApiConsumes,
  ApiOperation,
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

  @Post()
  @ApiOperation({ operationId: 'delete-media' })
  @ApiBody({
    description: 'File data',
    type: MediaSupabaseDto,
  })
  @ApiResponse({ status: 200 })
  deleteFile(@Body() media: MediaSupabaseDto) {
    return this.mediaService.deleteFile(media);
  }
}
