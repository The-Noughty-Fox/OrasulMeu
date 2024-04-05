import { ApiProperty } from '@nestjs/swagger';
import { File } from '@/shared/types';

export class MediaDto {
  @ApiProperty({ type: File, isArray: true })
  files: Express.Multer.File[];
}
