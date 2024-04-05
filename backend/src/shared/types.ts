import { Readable } from 'stream';
import { ApiProperty } from '@nestjs/swagger';

export enum SocialMedia {
  Google = 'google',
  Apple = 'apple',
  Facebook = 'facebook',
}

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
