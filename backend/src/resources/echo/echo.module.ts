import { Logger, Module } from '@nestjs/common';
import { EchoService } from './echo.service';
import { EchoController } from './echo.controller';
import { InfrastructureModule } from '../../infrastructure/infrastructure.module';

@Module({
  imports: [InfrastructureModule],
  controllers: [EchoController],
  providers: [EchoService, Logger],
})
export class EchoModule {}
