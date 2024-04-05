import { Controller, Get, Logger } from '@nestjs/common';
import { EchoService } from './echo.service';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';

// @UseGuards(JwtAuthGuard)
@Controller('echo')
@ApiTags('echo')
export class EchoController {
  constructor(
    private readonly echoService: EchoService,
    private readonly logger: Logger,
  ) {}

  @Get()
  @ApiOperation({ operationId: 'get-echo' })
  @ApiResponse({ type: String })
  echo(): string {
    // this.logger.log('EchoController.echo');
    // this.logger.warn('EchoController.echo', EchoController.name);
    // this.logger.error('EchoController.echo', EchoController.name);
    try {
      throw new Error('Test error');
      return this.echoService.echo();
    } catch (error) {
      this.logger.error('EchoController.echo', error.stack);
      this.logger.error(error);
      throw error;
    }
  }
}
