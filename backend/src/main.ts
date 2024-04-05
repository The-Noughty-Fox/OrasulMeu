import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import {
  DocumentBuilder,
  SwaggerDocumentOptions,
  SwaggerModule,
} from '@nestjs/swagger';
import * as yaml from 'yaml';
import * as fs from 'fs';
import * as path from 'path';
import * as cookieParser from 'cookie-parser';
import { AppLogger } from './infrastructure/logging/app-logger';

async function bootstrap() {
  const app = await NestFactory.create(AppModule, {
    bufferLogs: true,
  });
  app.use(cookieParser());
  app.useLogger(new AppLogger());
  const config = new DocumentBuilder()
    .setTitle('Orasul Meu')
    .setDescription('Orasul Meu Swagger API')
    .setVersion('1.0')
    .addTag('orasul_meu')
    .build();

  const options: SwaggerDocumentOptions = {
    operationIdFactory: (controllerKey: string, methodKey: string) => methodKey,
  };

  const document = SwaggerModule.createDocument(app, config, options);
  const yamlString: string = yaml.stringify(document);
  const filePath = path.join(__dirname, '..', 'swagger-spec.yaml');
  fs.writeFileSync(filePath, yamlString);
  SwaggerModule.setup('api', app, document);

  await app.listen(process.env.PORT);
}
bootstrap();
