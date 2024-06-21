import { AutoMap } from '@automapper/classes';
import {
  CreateDateColumn,
  DeleteDateColumn,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';

export class BaseEntity {
  @PrimaryGeneratedColumn()
  @AutoMap()
  id: number;

  @AutoMap()
  @CreateDateColumn()
  createDate: Date;

  @AutoMap()
  @UpdateDateColumn()
  updateDate: Date;

  @AutoMap()
  @DeleteDateColumn()
  deleteDate: Date;
}
