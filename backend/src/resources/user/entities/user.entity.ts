import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { AutoMap } from '@automapper/classes';

@Entity({ name: 'users' })
export class User {
  @PrimaryGeneratedColumn()
  @AutoMap()
  id: number;

  @Column()
  @AutoMap()
  email: string;

  @Column()
  @AutoMap()
  firstName: string;

  @Column()
  @AutoMap()
  lastName: string;

  @Column({ nullable: true })
  @AutoMap()
  apple_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  google_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  facebook_token?: string;

  @Column({ nullable: true })
  @AutoMap()
  socialProfilePictureUrl?: string;
}
