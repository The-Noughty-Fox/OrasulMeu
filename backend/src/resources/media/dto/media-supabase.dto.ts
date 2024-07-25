import { ApiProperty } from '@nestjs/swagger';
import { MediaType } from '../types';

export class MediaSupabaseDto {
  @ApiProperty({ type: 'integer' })
  id: number;

  @ApiProperty({ enum: MediaType })
  type: MediaType;

  @ApiProperty({ type: 'string', description: 'public url of media file' })
  url: string;

  @ApiProperty({
    type: 'string',
    description: 'path to the media file from the bucket',
  })
  bucketPath: string;

  @ApiProperty({ type: 'string', description: 'name of the file' })
  fileName: string;
}
