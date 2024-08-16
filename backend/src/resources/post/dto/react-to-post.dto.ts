import { ApiProperty } from '@nestjs/swagger';
import { Reaction, reactions } from '@/shared/types';
import { IsIn } from 'class-validator';

export class ReactToPostDto {
  @ApiProperty({ enum: Reaction })
  @IsIn(reactions)
  reaction: Reaction;
}
