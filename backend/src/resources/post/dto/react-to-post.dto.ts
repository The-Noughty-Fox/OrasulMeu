import { ApiProperty } from '@nestjs/swagger';
import { Reaction, reactions } from '@/shared/types';
import { IsIn } from 'class-validator';

export class ReactToPostDto {
  @ApiProperty({ name: 'react', enum: Reaction })
  @IsIn(reactions)
  reaction: Reaction;
}
