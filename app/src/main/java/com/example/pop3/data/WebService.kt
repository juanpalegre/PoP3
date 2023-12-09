package com.example.pop3.data

import com.example.pop3.models.ImageData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface WebService {
    @POST("/receive-image")
    fun sendImageData(@Body data: ImageData): Call<Void>
}

object RetrofitClient {
    private const val BASE_URL = "http://localhost:3000"
    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}