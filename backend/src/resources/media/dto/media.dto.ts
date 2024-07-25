import { ApiProperty } from '@nestjs/swagger';

export class MediaDto {
  @ApiProperty({ type: 'string', format: 'binary', isArray: true })
  files: any[];
}

export class MediaSingleDto {
  @ApiProperty({ type: 'string', format: 'binary' })
  file: any;
}
