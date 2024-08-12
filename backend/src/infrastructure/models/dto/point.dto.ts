import { ApiProperty } from '@nestjs/swagger';
import { IsLatitude, IsLongitude, IsNumber } from 'class-validator';

export class PointDto {
  constructor(obj: Partial<PointDto>) {
    Object.assign(this, obj);
  }

  @ApiProperty({ type: 'number', format: 'double' })
  @IsLatitude()
  @IsNumber()
  latitude: number;

  @ApiProperty({ type: 'number', format: 'double' })
  @IsLongitude()
  @IsNumber()
  longitude: number;
}
