import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  Res,
  UploadedFiles,
} from '@nestjs/common';
import { MediaService } from './media.service';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import { ConfigService } from '@nestjs/config';

@Controller('media')
export class MediaController {
  constructor(
    private readonly mediaService: MediaService,
    private configService: ConfigService,
  ) {}

  @Post()
  @UseInterceptors(
    FileInterceptor('file', {
      limits: { fileSize: 1024 * 1024 * 50 }, // 50MB
      storage: MediaService.getStorage(),
    }),
  )
  upload(
    @UploadedFile() file: Express.Multer.File,
    @Res({ passthrough: true }) res,
  ) {
    res.json(this.mediaService.create([file]));
  }

  @Post('files')
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: 1024 * 1024 * 50 }, // 50MB
      storage: MediaService.getStorage(),
    }),
  )
  uploadFiles(
    @UploadedFiles() files: Express.Multer.File[],
    @Res({ passthrough: true }) res,
  ) {
    res.json(this.mediaService.create(files));
  }
}
