package com.example.imagetopdf.network

import com.example.imagetopdf.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
object SupabaseClient {
    val supabaseUrl = BuildConfig.SUPABASE_URL
    val supabaseKey = BuildConfig.SUPABASE_KEY

    val client = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Auth)
    }
}