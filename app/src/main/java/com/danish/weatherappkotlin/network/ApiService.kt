package com.danish.weatherappkotlin.network

import com.danish.weatherappkotlin.BuildConfig.API_KEY
import com.danish.weatherappkotlin.WeatherResponse
import com.danish.weatherappkotlin.data.ForecastWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    fun weatherByCity(
        @Query("q") city: String,
        @Query("appid") api_key: String = API_KEY
    ) : Call<WeatherResponse>

    @GET("forecast")
    fun ForecastByCity(
        @Query("q") city: String,
        @Query("appid") api_key: String = API_KEY
    ) : Call<ForecastWeatherResponse>

    @GET("weather")
    fun weatherByCurrentLocation(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("appid") api_key: String = API_KEY
    ): Call<WeatherResponse>

    @GET("forecast")
    fun forecastByCurrentLocation(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("appid") api_key: String = API_KEY
    ): Call<ForecastWeatherResponse>

}