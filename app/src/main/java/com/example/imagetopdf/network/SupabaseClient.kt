package com.example.imagetopdf.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = SupabaseKeys.URL,
        supabaseKey = SupabaseKeys.KEY
    ){
        install(Auth)
    }
}