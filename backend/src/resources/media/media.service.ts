import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { v4 as uuidv4 } from 'uuid';
import * as path from 'path';
import { ConfigService } from '@nestjs/config';
import { InjectRepository } from '@nestjs/typeorm';
import { Media } from '@/resources/media/entities/media.entity';
import { Repository } from 'typeorm';
import { allowedMimeTypes, MediaType } from '@/resources/media/types';
import { SupabaseService } from '../supabase/supabase.service';
import { SupabaseClient } from '@supabase/supabase-js';

@Injectable()
export class MediaService {
  private readonly supabase: SupabaseClient;

  constructor(
    private configService: ConfigService,
    @InjectRepository(Media) private repository: Repository<Media>,
    private readonly supabaseService: SupabaseService,
  ) {
    this.supabase = this.supabaseService.getClient();
  }

  async create(files: Express.Multer.File[]) {
    const results = [];
    let fileType: MediaType;

    for (const file of files) {
      const filename = `${uuidv4()}${path.extname(file.originalname)}`;

      // get file type
      if (allowedMimeTypes.images.includes(file.mimetype)) {
        fileType = MediaType.Image;
      } else if (allowedMimeTypes.videos.includes(file.mimetype)) {
        fileType = MediaType.Video;
      } else {
        throw new BadRequestException('Invalid file type');
      }

      // upload file to bucket
      const { data: uploadData, error: uploadError } =
        await this.supabase.storage
          .from('OrasulMeu')
          .upload(`/${fileType}s/post-${fileType}s/${filename}`, file.buffer);

      if (uploadError || !uploadData) {
        throw new InternalServerErrorException('Error uploading file');
      }

      const filePath = uploadData.path;

      // generate public url
      const { data: publicUrl } = this.supabase.storage
        .from('OrasulMeu')
        .getPublicUrl(filePath);

      // get file entity
      const { data, error } = await this.supabase
        .from('media')
        .insert([
          {
            url: publicUrl.publicUrl,
            type: fileType,
            bucketPath: filePath,
            fileName: filename,
          },
        ])
        .select('*');

      if (error || !data || data.length === 0) {
        throw new InternalServerErrorException('Error saving file');
      }

      results.push(data[0]);
    }

    return results;
  }
}
