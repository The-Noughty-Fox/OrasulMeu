import { getSchemaPath } from '@nestjs/swagger';
import {
  ReferenceObject,
  SchemaObject,
} from '@nestjs/swagger/dist/interfaces/open-api-spec.interface';

export const getPaginationSchema = (
  // eslint-disable-next-line @typescript-eslint/ban-types
  model: string | Function,
  example?: Record<string, string>[],
) =>
  ({
    data: {
      type: 'array',
      items: { $ref: getSchemaPath(model) },
      example,
    },
    total: {
      type: 'number',
    },
    page: {
      type: 'number',
    },
    limit: {
      type: 'number',
    },
  }) as SchemaObject & Partial<ReferenceObject>;
