import { Injectable } from '@nestjs/common';
import { diskStorage } from 'multer';
import { v4 as uuidv4 } from 'uuid';
import * as path from 'path';
import { ConfigService } from '@nestjs/config';
import { InjectRepository } from '@nestjs/typeorm';
import { Media } from '@/resources/media/entities/media.entity';
import { Repository } from 'typeorm';
import { MediaType } from '@/resources/media/types';

@Injectable()
export class MediaService {
  constructor(
    private configService: ConfigService,
    @InjectRepository(Media) private repository: Repository<Media>,
  ) {}

  static getStorage() {
    return diskStorage({
      destination: './uploads',
      filename: (req, file, callback) => {
        const fileExtName = path.extname(file.originalname);
        const fileName = uuidv4() + fileExtName; // Generate a unique filename
        callback(null, fileName);
      },
    });
  }

  getUploadPath(filename: string) {
    return `${this.configService.get<string>('API_BASE')}/uploads/${filename}`;
  }

  create(files: Express.Multer.File[]) {
    const media = files.map((file) => ({
      fileName: file.filename,
      type: MediaType.Image,
      url: this.getUploadPath(file.filename),
    }));

    return this.repository.save(this.repository.create(media));
  }
}
