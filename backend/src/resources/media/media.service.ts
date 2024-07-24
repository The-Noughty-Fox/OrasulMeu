import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { v4 as uuidv4 } from 'uuid';
import * as path from 'path';
import { allowedMimeTypes, MediaType } from '@/resources/media/types';
import { SupabaseService } from '../supabase/supabase.service';

@Injectable()
export class MediaService {
  constructor(private readonly supabaseService: SupabaseService) {}

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
        await this.supabaseService
          .getClient()
          .storage.from('OrasulMeu')
          .upload(`/${fileType}s/post-${fileType}s/${filename}`, file.buffer);

      if (uploadError || !uploadData) {
        throw new InternalServerErrorException('Error uploading file');
      }

      const filePath = uploadData.path;

      // generate public url
      const { data: publicUrl } = this.supabaseService
        .getClient()
        .storage.from('OrasulMeu')
        .getPublicUrl(filePath);

      // get file entity
      const { data, error } = await this.supabaseService
        .getClient()
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
