import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import * as chalk from 'chalk';
import { formatDate } from '../../helpers/date';

@Injectable()
export class LoggingMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction): void {
    const startTime = Date.now();
    res.on('finish', () => {
      const duration = Date.now() - startTime;
      const method = req.method;
      const url = req.originalUrl || req.url;
      const status = res.statusCode;

      console.log(
        `${chalk.blue(formatDate(new Date()))} ${chalk.green(method)} ${url} ${this.getStatusColor(status)(status.toString())} ${chalk.magenta(`${duration}ms`)}`,
      );
      console.log(JSON.stringify(req.headers, null, 2));
    });
    next();
  }

  private getStatusColor(status: number) {
    if (status >= 500) return chalk.red;
    if (status >= 400) return chalk.yellow;
    if (status >= 300) return chalk.cyan;
    return chalk.green;
  }
}
