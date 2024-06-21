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
import { ApiConsumes } from '@nestjs/swagger';
import { mbToBytes } from '@/helpers/other';

@Controller('media')
export class MediaController {
  constructor(
    private readonly mediaService: MediaService,
    private configService: ConfigService,
  ) {}

  @Post()
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FileInterceptor('file', {
      limits: { fileSize: mbToBytes(50) },
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
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FilesInterceptor('files', 20, {
      limits: { fileSize: mbToBytes(50) },
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
