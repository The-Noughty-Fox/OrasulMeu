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
          userId: number | null;
        };
        Insert: {
          content?: string | null;
          createdAt?: string;
          id?: number;
          location?: unknown | null;
          locationAddress?: string | null;
          title?: string;
          userId?: number | null;
        };
        Update: {
          content?: string | null;
          createdAt?: string;
          id?: number;
          location?: unknown | null;
          locationAddress?: string | null;
          title?: string;
          userId?: number | null;
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
      get_post_by_id: {
        Args: {
          post_id: number;
        };
        Returns: Json;
      };
      get_posts: {
        Args: {
          page_input: number;
          limit_input: number;
        };
        Returns: Json;
      };
      get_posts_by_user_id: {
        Args: {
          user_id_input: number;
        };
        Returns: Json;
      };
      get_posts_for_user: {
        Args: {
          user_id_input: number;
          page_input: number;
          limit_input: number;
        };
        Returns: Json;
      };
      get_reaction_count: {
        Args: {
          post_id_input: number;
          reaction_input: Database['public']['Enums']['reaction'];
        };
        Returns: number;
      };
      get_user_profile: {
        Args: {
          user_id_input: number;
        };
        Returns: {
          id: number;
          email: string;
          firstName: string;
          lastName: string;
          socialProfilePictureUrl: string;
          postsCount: number;
          postsReactionsCount: number;
        }[];
      };
    };
    Enums: {
      media_type: 'image' | 'video';
      reaction: 'like' | 'dislike';
    };
    CompositeTypes: {
      [_ in never]: never;
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
