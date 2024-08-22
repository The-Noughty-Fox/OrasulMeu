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
    type: 'object',
    properties: {
      data: {
        type: 'array',
        items: { $ref: getSchemaPath(model) },
        example,
      },
      total: {
        type: 'integer',
      },
      page: {
        type: 'integer',
      },
      limit: {
        type: 'integer',
      },
    },
  }) as SchemaObject & Partial<ReferenceObject>;
