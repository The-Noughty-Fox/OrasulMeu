export enum SocialMedia {
  Google = 'google',
  Apple = 'apple',
  Facebook = 'facebook',
}

export type Coordinate = number[];

export enum Reaction {
  Like = 'like',
  Dislike = 'dislike',
}

export const reactions = [Reaction.Like, Reaction.Dislike] as const;

export type ReactionType = (typeof reactions)[number];
