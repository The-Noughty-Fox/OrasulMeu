export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[];

export type Database = {
  public: {
    Tables: {
      comments: {
        Row: {
          body: string;
          id: number;
          postId: number;
          userId: number;
        };
        Insert: {
          body: string;
          id?: number;
          postId: number;
          userId: number;
        };
        Update: {
          body?: string;
          id?: number;
          postId?: number;
          userId?: number;
        };
        Relationships: [
          {
            foreignKeyName: 'comments_postId_fkey';
            columns: ['postId'];
            isOneToOne: false;
            referencedRelation: 'posts';
            referencedColumns: ['id'];
          },
          {
            foreignKeyName: 'comments_userId_fkey';
            columns: ['userId'];
            isOneToOne: false;
            referencedRelation: 'custom_users';
            referencedColumns: ['id'];
          },
        ];
      };
      custom_users: {
        Row: {
          appleToken: string | null;
          email: string;
          facebookToken: string | null;
          googleToken: string | null;
          id: number;
          socialProfilePictureUrl: string | null;
          username: string;
        };
        Insert: {
          appleToken?: string | null;
          email: string;
          facebookToken?: string | null;
          googleToken?: string | null;
          id?: number;
          socialProfilePictureUrl?: string | null;
          username: string;
        };
        Update: {
          appleToken?: string | null;
          email?: string;
          facebookToken?: string | null;
          googleToken?: string | null;
          id?: number;
          socialProfilePictureUrl?: string | null;
          username?: string;
        };
        Relationships: [];
      };
      media: {
        Row: {
          bucketPath: string;
          fileName: string;
          id: number;
          type: Database['public']['Enums']['media_type'];
          url: string;
        };
        Insert: {
          bucketPath: string;
          fileName: string;
          id?: number;
          type: Database['public']['Enums']['media_type'];
          url: string;
        };
        Update: {
          bucketPath?: string;
          fileName?: string;
          id?: number;
          type?: Database['public']['Enums']['media_type'];
          url?: string;
        };
        Relationships: [];
      };
      post_media: {
        Row: {
          id: number;
          mediaId: number;
          postId: number;
        };
        Insert: {
          id?: number;
          mediaId: number;
          postId: number;
        };
        Update: {
          id?: number;
          mediaId?: number;
          postId?: number;
        };
        Relationships: [
          {
            foreignKeyName: 'post_media_mediaId_fkey';
            columns: ['mediaId'];
            isOneToOne: false;
            referencedRelation: 'media';
            referencedColumns: ['id'];
          },
          {
            foreignKeyName: 'post_media_postId_fkey';
            columns: ['postId'];
            isOneToOne: false;
            referencedRelation: 'posts';
            referencedColumns: ['id'];
          },
        ];
      };
      post_reactions: {
        Row: {
          id: number;
          postId: number;
          reaction: Database['public']['Enums']['reaction'];
          userId: number;
        };
        Insert: {
          id?: number;
          postId: number;
          reaction: Database['public']['Enums']['reaction'];
          userId: number;
        };
        Update: {
          id?: number;
          postId?: number;
          reaction?: Database['public']['Enums']['reaction'];
          userId?: number;
        };
        Relationships: [
          {
            foreignKeyName: 'post_reactions_postId_fkey';
            columns: ['postId'];
            isOneToOne: false;
            referencedRelation: 'posts';
            referencedColumns: ['id'];
          },
          {
            foreignKeyName: 'post_reactions_userId_fkey';
            columns: ['userId'];
            isOneToOne: false;
            referencedRelation: 'custom_users';
            referencedColumns: ['id'];
          },
        ];
      };
      posts: {
        Row: {
          content: string | null;
          createdAt: string;
          id: number;
          location: unknown | null;
          locationAddress: string | null;
          title: string;
          userId: number;
        };
        Insert: {
          content?: string | null;
          createdAt?: string;
          id?: number;
          location?: unknown | null;
          locationAddress?: string | null;
          title?: string;
          userId: number;
        };
        Update: {
          content?: string | null;
          createdAt?: string;
          id?: number;
          location?: unknown | null;
          locationAddress?: string | null;
          title?: string;
          userId?: number;
        };
        Relationships: [
          {
            foreignKeyName: 'posts_userId_fkey';
            columns: ['userId'];
            isOneToOne: false;
            referencedRelation: 'custom_users';
            referencedColumns: ['id'];
          },
        ];
      };
    };
    Views: {
      [_ in never]: never;
    };
    Functions: {
      count_posts: {
        Args: Record<PropertyKey, never>;
        Returns: number;
      };
      count_posts_for_user: {
        Args: {
          user_id_input: number;
        };
        Returns: number;
      };
      count_posts_with_phrase: {
        Args: {
          phrase_input: string;
        };
        Returns: number;
      };
      count_reactions_for_post: {
        Args: {
          post_id_input: number;
        };
        Returns: number;
      };
      custom_count_posts_for_user: {
        Args: {
          user_id_input: number;
        };
        Returns: number;
      };
      get_media_by_post_id: {
        Args: {
          post_id_input: number;
        };
        Returns: {
          id: number;
          type: Database['public']['Enums']['media_type'];
          url: string;
          fileName: string;
        }[];
      };
      get_post: {
        Args: {
          post_id_input: number;
          user_id_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type'];
      };
      get_post_anonymous:
        | {
            Args: Record<PropertyKey, never>;
            Returns: Database['public']['CompositeTypes']['post_type_anonymous'];
          }
        | {
            Args: {
              post_id_input: number;
            };
            Returns: Database['public']['CompositeTypes']['post_type_anonymous'];
          };
      get_posts: {
        Args: {
          limit_input: number;
          page_input: number;
          user_id_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type'][];
      };
      get_posts_anonymous: {
        Args: {
          page_input: number;
          limit_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type_anonymous'][];
      };
      get_posts_by_reactions: {
        Args: {
          limit_input: number;
          page_input: number;
          user_id_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type'][];
      };
      get_posts_by_reactions_count_anonymous: {
        Args: {
          limit_input: number;
          page_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type_anonymous'][];
      };
      get_posts_for_user: {
        Args: {
          user_id_input: number;
          page_input: number;
          limit_input: number;
        };
        Returns: Json;
      };
      get_profile_by_id: {
        Args: {
          user_id_input: number;
        };
        Returns: {
          id: number;
          email: string;
          username: string;
          socialProfilePictureUrl: string;
          postsCount: number;
          reactionsCount: number;
        }[];
      };
      get_reaction_count: {
        Args: {
          post_id_input: number;
          reaction_input: Database['public']['Enums']['reaction'];
        };
        Returns: number;
      };
      get_user_posts: {
        Args: {
          user_id_input: number;
          limit_input: number;
          page_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type'][];
      };
      search_posts_by_phrase: {
        Args: {
          user_id_input: number;
          phrase_input: string;
          limit_input: number;
          page_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type'][];
      };
      search_posts_by_phrase_anonymous: {
        Args: {
          phrase_input: string;
          limit_input: number;
          page_input: number;
        };
        Returns: Database['public']['CompositeTypes']['post_type_anonymous'][];
      };
    };
    Enums: {
      media_type: 'image' | 'video';
      reaction: 'like' | 'dislike';
    };
    CompositeTypes: {
      anonymous_post_type: {
        id: number | null;
        title: string | null;
        content: string | null;
        locationaddress: string | null;
        location: Database['public']['CompositeTypes']['location_type'] | null;
        createdat: string | null;
        author: Database['public']['CompositeTypes']['author_type'] | null;
        reactions:
          | Database['public']['CompositeTypes']['anonymous_reaction_type']
          | null;
        comments: Database['public']['CompositeTypes']['comment_type'][] | null;
        media:
          | Database['public']['CompositeTypes']['media_type_for_post'][]
          | null;
      };
      anonymous_reaction_type: {
        like: number | null;
        dislike: number | null;
      };
      author_type: {
        id: number | null;
        email: string | null;
        username: string | null;
        socialProfilePictureUrl: string | null;
      };
      comment_type: {
        id: number | null;
        body: string | null;
        author: Database['public']['CompositeTypes']['author_type'] | null;
      };
      location_type: {
        longitude: number | null;
        latitude: number | null;
      };
      media_type_for_post: {
        id: number | null;
        type: Database['public']['Enums']['media_type'] | null;
        url: string | null;
        fileName: string | null;
        bucketPath: string | null;
      };
      post_type: {
        id: number | null;
        title: string | null;
        content: string | null;
        locationAddress: string | null;
        location: Database['public']['CompositeTypes']['location_type'] | null;
        createdAt: string | null;
        author: Database['public']['CompositeTypes']['author_type'] | null;
        reactions:
          | Database['public']['CompositeTypes']['reactions_type']
          | null;
        comments: Database['public']['CompositeTypes']['comment_type'][] | null;
        media:
          | Database['public']['CompositeTypes']['media_type_for_post'][]
          | null;
      };
      post_type_anonymous: {
        id: number | null;
        title: string | null;
        content: string | null;
        locationAddress: string | null;
        location: Database['public']['CompositeTypes']['location_type'] | null;
        createdAt: string | null;
        author: Database['public']['CompositeTypes']['author_type'] | null;
        reactions:
          | Database['public']['CompositeTypes']['anonymous_reaction_type']
          | null;
        comments: Database['public']['CompositeTypes']['comment_type'][] | null;
        media:
          | Database['public']['CompositeTypes']['media_type_for_post'][]
          | null;
      };
      reactions_type: {
        like: number | null;
        dislike: number | null;
        userReaction: Database['public']['Enums']['reaction'] | null;
      };
    };
  };
};

type PublicSchema = Database[Extract<keyof Database, 'public'>];

export type Tables<
  PublicTableNameOrOptions extends
    | keyof (PublicSchema['Tables'] & PublicSchema['Views'])
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof (Database[PublicTableNameOrOptions['schema']]['Tables'] &
        Database[PublicTableNameOrOptions['schema']]['Views'])
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? (Database[PublicTableNameOrOptions['schema']]['Tables'] &
      Database[PublicTableNameOrOptions['schema']]['Views'])[TableName] extends {
      Row: infer R;
    }
    ? R
    : never
  : PublicTableNameOrOptions extends keyof (PublicSchema['Tables'] &
        PublicSchema['Views'])
    ? (PublicSchema['Tables'] &
        PublicSchema['Views'])[PublicTableNameOrOptions] extends {
        Row: infer R;
      }
      ? R
      : never
    : never;

export type TablesInsert<
  PublicTableNameOrOptions extends
    | keyof PublicSchema['Tables']
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions['schema']]['Tables']
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions['schema']]['Tables'][TableName] extends {
      Insert: infer I;
    }
    ? I
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema['Tables']
    ? PublicSchema['Tables'][PublicTableNameOrOptions] extends {
        Insert: infer I;
      }
      ? I
      : never
    : never;

export type TablesUpdate<
  PublicTableNameOrOptions extends
    | keyof PublicSchema['Tables']
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions['schema']]['Tables']
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions['schema']]['Tables'][TableName] extends {
      Update: infer U;
    }
    ? U
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema['Tables']
    ? PublicSchema['Tables'][PublicTableNameOrOptions] extends {
        Update: infer U;
      }
      ? U
      : never
    : never;

export type Enums<
  PublicEnumNameOrOptions extends
    | keyof PublicSchema['Enums']
    | { schema: keyof Database },
  EnumName extends PublicEnumNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicEnumNameOrOptions['schema']]['Enums']
    : never = never,
> = PublicEnumNameOrOptions extends { schema: keyof Database }
  ? Database[PublicEnumNameOrOptions['schema']]['Enums'][EnumName]
  : PublicEnumNameOrOptions extends keyof PublicSchema['Enums']
    ? PublicSchema['Enums'][PublicEnumNameOrOptions]
    : never;
