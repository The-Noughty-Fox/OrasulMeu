import { Database } from '@/supabase';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { createClient, SupabaseClient } from '@supabase/supabase-js';

@Injectable()
export class SupabaseService {
  private supabase: SupabaseClient<Database>;

  constructor(private readonly configService: ConfigService) {}

  getClient(): SupabaseClient<Database> {
    if (this.supabase) {
      return this.supabase;
    }

    const supabaseUrl = this.configService.get<string>('SUPABASE_URL');
    const supabaseKey = this.configService.get<string>(
      'SUPABASE_SERVICE_ROLE_KEY',
    );
    this.supabase = createClient<Database>(supabaseUrl, supabaseKey);

    return this.supabase;
  }
}
