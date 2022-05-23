package com.danish.weatherappkotlin.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danish.weatherappkotlin.WeatherResponse
import com.danish.weatherappkotlin.data.ForecastWeatherResponse
import com.danish.weatherappkotlin.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    val weatherByCity = MutableLiveData<WeatherResponse>()
    val forecastByCity = MutableLiveData<ForecastWeatherResponse>()

    val weatherByCurrentLocation = MutableLiveData<WeatherResponse>()
    val forecastByCurrentLocation = MutableLiveData<ForecastWeatherResponse>()

    fun weatherByCity(city: String){
        ApiConfig.getApiService().weatherByCity(city).enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>,
            ){
              if (response.isSuccessful){
                  weatherByCity.postValue(response.body())
              }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Failure", t.message.toString() )
            }

        })
    }

    fun getWeatherByCity() : LiveData<WeatherResponse> = weatherByCity

    fun forecastByCity(city: String) {
        ApiConfig.getApiService().ForecastByCity(city).enqueue(object : Callback<ForecastWeatherResponse>{
            override fun onResponse(
                call: Call<ForecastWeatherResponse>,
                response: Response<ForecastWeatherResponse>,
            ){
                if (response.isSuccessful){
                    forecastByCity.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<ForecastWeatherResponse>, t: Throwable) {
                Log.e("Failure", t.message.toString() )
            }

        })
    }
    fun getForecastWeatherByCity() : LiveData<ForecastWeatherResponse> = forecastByCity

    fun weatherByCurrentLocation(lat: Double, lon: Double){
        ApiConfig.getApiService().weatherByCurrentLocation(lat, lon).enqueue(object :
        Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                weatherByCurrentLocation.postValue(response.body())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    fun getWeatherByCurrentLocation(): LiveData<WeatherResponse> = weatherByCurrentLocation

    fun forecastByCurrentLocation(lat: Double, lon: Double){
        ApiConfig.getApiService().forecastByCurrentLocation(lat, lon).enqueue(object :
            Callback<ForecastWeatherResponse>{
            override fun onResponse(
                call: Call<ForecastWeatherResponse>,
                response: Response<ForecastWeatherResponse>
            ) {
                forecastByCurrentLocation.postValue(response.body())
            }

            override fun onFailure(call: Call<ForecastWeatherResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}