import { ApiProperty } from '@nestjs/swagger';
import { Readable } from 'stream';

export class File {
  @ApiProperty()
  fieldname: string;

  @ApiProperty()
  originalname: string;

  @ApiProperty()
  encoding: string;

  @ApiProperty()
  mimetype: string;

  @ApiProperty({ type: 'integer' })
  size: number;

  @ApiProperty({ type: 'string', format: 'binary' })
  stream: Readable;

  @ApiProperty()
  destination: string;

  @ApiProperty()
  filename: string;

  @ApiProperty()
  path: string;

  @ApiProperty({ type: 'string', format: 'binary' })
  buffer: Buffer;
}
