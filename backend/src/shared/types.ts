import { Readable } from 'stream';
import { ApiProperty } from '@nestjs/swagger';

export enum SocialMedia {
  Google = 'google',
  Apple = 'apple',
  Facebook = 'facebook',
}

export type Coordinate = number[];
