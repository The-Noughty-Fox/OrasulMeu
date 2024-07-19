export enum MediaType {
  Image = 'image',
  Video = 'video',
}

export const allowedMimeTypes = {
  images: [
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp',
    'image/svg+xml',
    'image/avif',
    'image/apng',
  ],
  videos: ['video/mp4', 'video/avi', 'video/mpeg', 'video/webm'],
};
