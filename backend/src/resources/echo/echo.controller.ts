import { Controller, Get } from '@nestjs/common';
import { EchoService } from './echo.service';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';

@Controller('echo')
@ApiTags('echo')
export class EchoController {
  constructor(private readonly echoService: EchoService) {}

  @Get()
  @ApiOperation({ operationId: 'get-echo' })
  @ApiResponse({ type: String })
  echo(): string {
    return this.echoService.echo();
  }
}
