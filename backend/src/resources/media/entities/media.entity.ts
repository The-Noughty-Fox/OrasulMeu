import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { AutoMap } from '@automapper/classes';
import { MediaType } from '@/resources/media/types';
import { ApiProperty } from '@nestjs/swagger';
import { BaseEntity } from '@/infrastructure/models/entities/base.model';

@Entity({ name: 'media' })
export class Media extends BaseEntity {
  @PrimaryGeneratedColumn()
  @AutoMap()
  @ApiProperty({ type: 'integer' })
  id: number;

  @Column()
  @AutoMap()
  @ApiProperty({ enum: MediaType })
  type: MediaType;

  @Column()
  @AutoMap()
  @ApiProperty()
  url: string;

  @Column()
  @AutoMap()
  @ApiProperty()
  fileName: string;
}
