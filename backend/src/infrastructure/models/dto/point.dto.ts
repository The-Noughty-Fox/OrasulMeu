import { ApiProperty } from '@nestjs/swagger';
import { IsNumber } from 'class-validator';

export class PointDto {
  constructor(obj: Partial<PointDto>) {
    Object.assign(this, obj);
  }

  @ApiProperty({ type: 'number', format: 'double' })
  @IsNumber()
  latitude: number;

  @ApiProperty({ type: 'number', format: 'double' })
  @IsNumber()
  longitude: number;
}
