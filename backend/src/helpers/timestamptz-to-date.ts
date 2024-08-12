export const timestamptzToDate = (timestamptz: string): Date | null => {
  try {
    if (typeof timestamptz !== 'string' || timestamptz.trim() === '') {
      throw new Error('TTimestamptz must be a non-empty string');
    }

    const date = new Date(timestamptz);
    return date;
  } catch (error) {
    console.error(error);
    return null;
  }
};
