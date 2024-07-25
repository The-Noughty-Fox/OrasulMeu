import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  UploadedFiles,
  UseGuards,
} from '@nestjs/common';
import { MediaService } from './media.service';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiConsumes,
  ApiInternalServerErrorResponse,
  ApiOkResponse,
  ApiTags,
} from '@nestjs/swagger';
import { mbToBytes } from '@/helpers/other';
import { JwtAuthGuard } from '../auth/passport/guards';
import { MediaSupabaseDto } from './dto/media-supabase.dto';
import { MediaDto, MediaSingleDto } from './dto/media.dto';

@UseGuards(JwtAuthGuard)
@ApiTags('media')
@Controller('media')
export class MediaController {
  constructor(private readonly mediaService: MediaService) {}

  @Post()
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    type: MediaSingleDto,
  })
  @ApiOkResponse({
    description: 'Media file uploaded successfully',
    type: MediaSupabaseDto,
    isArray: true,
  })
  @ApiBadRequestResponse({ description: 'Bad Request' })
  @ApiInternalServerErrorResponse({ description: 'Internal Server Error' })
  @UseInterceptors(
    FileInterceptor('file', {
      limits: { fileSize: mbToBytes(50) },
    }),
  )
  upload(@UploadedFile() file: Express.Multer.File) {
    return this.mediaService.create([file]);
  }

  @Post('files')
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    type: MediaDto,
  })
  @ApiOkResponse({
    description: 'Media files uploaded successfully',
    type: MediaSupabaseDto,
    isArray: true,
  })
  @ApiBadRequestResponse({ description: 'Bad Request' })
  @ApiInternalServerErrorResponse({ description: 'Internal Server Error' })
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: mbToBytes(50) },
    }),
  )
  uploadFiles(@UploadedFiles() files: Express.Multer.File[]) {
    return this.mediaService.create(files);
  }
}
