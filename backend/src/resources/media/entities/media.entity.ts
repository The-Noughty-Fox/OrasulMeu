import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { AutoMap } from '@automapper/classes';
import { MediaType } from '@/resources/media/types';

@Entity({ name: 'media' })
export class Media {
  @PrimaryGeneratedColumn()
  @AutoMap()
  id: number;

  @Column()
  @AutoMap()
  type: MediaType;

  @Column()
  @AutoMap()
  url: string;

  @Column()
  @AutoMap()
  fileName: string;
}
