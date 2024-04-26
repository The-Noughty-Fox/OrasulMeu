import { Exclude, Expose } from 'class-transformer';
import { Coordinate } from '@/shared/types';

export class PointGeography {
  type: 'Point';
  coordinates: Coordinate;
}

@Exclude()
export class Point {
  @Expose()
  readonly coordinates: Coordinate;

  constructor(coordinates: Coordinate) {
    this.coordinates = coordinates;
  }

  isEmpty(): boolean {
    return false;
  }

  gis(): string {
    return `POINT(${this.coordinates.join(' ')})`;
  }

  geography(): PointGeography {
    return {
      type: 'Point',
      coordinates: this.coordinates,
    };
  }
}
