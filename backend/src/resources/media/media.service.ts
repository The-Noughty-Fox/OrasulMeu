import { BadRequestException, Injectable } from '@nestjs/common';
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

  getUploadPath(filename: string) {
    return `${this.configService.get<string>('API_BASE')}/uploads/${filename}`;
  }

  async create(files: Express.Multer.File[]) {
    const results = [];
    let fileType: MediaType;
    let bucketPath: string;

    for (const file of files) {
      const filename = `${uuidv4()}${path.extname(file.originalname)}`;

      if (allowedMimeTypes.images.includes(file.mimetype)) {
        fileType = MediaType.Image;
        bucketPath = '/images/post-images/';
      } else if (allowedMimeTypes.videos.includes(file.mimetype)) {
        fileType = MediaType.Video;
        bucketPath = '/videos/post-videos/';
      } else {
        throw new BadRequestException('Invalid file type');
      }

      const { data: uploadData, error: uploadError } =
        await this.supabase.storage
          .from('OrasulMeu')
          .upload(`${bucketPath}${filename}`, file.buffer);

      if (uploadError || !uploadData) {
        throw new BadRequestException('Error uploading file');
      }

      const filePath = uploadData.path;

      const { data: publicUrl } = this.supabase.storage
        .from('OrasulMeu')
        .getPublicUrl(filePath);

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
        throw new BadRequestException('Error saving file');
      }

      results.push(data[0]);
    }

    return results;
  }
}
