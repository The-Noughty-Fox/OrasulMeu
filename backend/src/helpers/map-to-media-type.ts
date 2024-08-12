import { MediaType } from '@/resources/media/types';

export const mapToMediaType = (
  type: 'image' | 'video' | null,
): MediaType | null => {
  if (type === 'image') return MediaType.Image;
  else if (type === 'video') return MediaType.Video;
  else return null;
};
