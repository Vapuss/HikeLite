package com.vapuss.hikelite.data.remote

import com.vapuss.hikelite.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("mountain") mountain: String
    ): WeatherResponse
}
