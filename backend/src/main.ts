import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import * as yaml from 'yaml';
import * as fs from 'fs';
import * as path from 'path';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const config = new DocumentBuilder()
    .setTitle('Orasul Meu')
    .setDescription('Orasul Meu Swagger API')
    .setVersion('1.0')
    .addTag('orasul_meu')
    .build();
  const document = SwaggerModule.createDocument(app, config);
  const yamlString: string = yaml.stringify(document);
  const filePath = path.join(__dirname, '..', 'swagger-spec.yaml');
  fs.writeFileSync(filePath, yamlString);
  SwaggerModule.setup('api', app, document);

  await app.listen(process.env.PORT);
}
bootstrap();
