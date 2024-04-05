import { ConsoleLogger, Injectable, Scope } from '@nestjs/common';
import * as chalk from 'chalk';
import { formatDate } from '../../helpers/date';

function formatMessage(message: string): string {
  return `${chalk.gray(`[${formatDate(new Date())}]`)} ${message}`;
}

@Injectable({ scope: Scope.TRANSIENT })
export class AppLogger extends ConsoleLogger {
  log(message: string) {
    console.log(formatMessage(chalk.blue(`[INFO] ${message}`)));
  }

  error(error: Error | string, trace?: string) {
    const message = typeof error === 'string' ? error : error.message;
    console.error(formatMessage(chalk.red(`[ERROR] ${message}`)));

    // Log the stack trace if available
    const stackTrace =
      error instanceof Error && error.stack ? error.stack : trace;
    if (stackTrace) {
      console.error(chalk.red(stackTrace));
    }
  }

  warn(message: string) {
    console.warn(formatMessage(chalk.yellow(`[WARN] ${message}`)));
  }
}
