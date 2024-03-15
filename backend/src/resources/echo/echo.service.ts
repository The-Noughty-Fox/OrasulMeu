import { Injectable } from '@nestjs/common';

@Injectable()
export class EchoService {
  echo(): string {
    return 'Hello, Orasul Meu!';
  }
}
