import { Reaction } from '@/shared/types';

export const mapToReaction = (
  reaction: 'like' | 'dislike' | null,
): Reaction | null => {
  if (reaction === 'like') return Reaction.Like;
  else if (reaction === 'dislike') return Reaction.Dislike;
  else return null;
};
