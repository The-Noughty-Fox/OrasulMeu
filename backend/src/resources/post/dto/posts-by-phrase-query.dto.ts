import { PaginationQueryDto } from '@/infrastructure/models/dto/pagination-query.dto';
import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString, Length } from 'class-validator';

export class PostsByPhraseQueryDto extends PaginationQueryDto {
  @IsNotEmpty()
  @IsString()
  @Length(1, 100)
  @ApiProperty({ required: true, type: 'string' })
  phrase: string;
}
