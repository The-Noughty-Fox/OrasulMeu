import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity({ name: 'users' })
export class User {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  email: string;

  @Column()
  firstName: string;

  @Column()
  lastName: string;

  @Column({ nullable: true })
  apple_token?: string;

  @Column({ nullable: true })
  google_token?: string;

  @Column({ nullable: true })
  facebook_token?: string;

  @Column({ nullable: true })
  socialProfilePictureUrl?: string;
}
