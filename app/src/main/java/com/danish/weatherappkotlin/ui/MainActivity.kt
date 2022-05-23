package com.danish.weatherappkotlin.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.danish.weatherappkotlin.BuildConfig
import com.danish.weatherappkotlin.R
import com.danish.weatherappkotlin.WeatherResponse
import com.danish.weatherappkotlin.data.ForecastWeatherResponse
import com.danish.weatherappkotlin.databinding.ActivityMainBinding
import com.danish.weatherappkotlin.utils.HelperFunction.formatterDegree
import com.danish.weatherappkotlin.utils.sizeIconWeather2x
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private lateinit var mAdapter: WeatherAdapter

    private var isLoading: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        searchCity()
        getWeatherByCity()

        mAdapter = WeatherAdapter()


        getWeatherByCurrentLocation()
    }

    private fun getWeatherByCity() {
        viewModel.getWeatherByCity().observe(this) {
            setupView(it, null)
        }

        viewModel.getForecastWeatherByCity().observe(this) {
            setupView(null, it)
            isLoading = false
            loadingStateView()
        }
    }

    fun setupView(weather: WeatherResponse?, forecastWeatherResponse: ForecastWeatherResponse?) {
        weather?.let {
            binding.apply {
                tvCity.text = it.name
                tvDegree.text = formatterDegree(it.main?.temp)

                val iconId = it.weather?.get(0)?.icon
                val iconUrl = BuildConfig.ICON_URL + iconId + sizeIconWeather2x
                Glide.with(this@MainActivity).load(iconUrl)
                    .into(imgIcWeather)

                setBackroundImage(it.weather?.get(0)?.id, iconId)
            }
        }
        forecastWeatherResponse.let {
            mAdapter.setData(it?.list)
            binding.rvWeather.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(
                    this.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

        }
    }

    private fun setBackroundImage(idWeather: Int?, icon: String?) {
        idWeather?.let {
            when (idWeather) {
                in resources.getIntArray(R.array.thunderstorm_id_list) ->
                    setImageBackround(R.drawable.thunderstorm)
                in resources.getIntArray(R.array.drizzle_id_list) ->
                    setImageBackround(R.drawable.drizzle)
                in resources.getIntArray(R.array.rain_id_list) ->
                    setImageBackround(R.drawable.rain)
                in resources.getIntArray(R.array.freezing_rain_id_list) ->
                    setImageBackround(R.drawable.freezing_rain)
                in resources.getIntArray(R.array.snow_id_list) ->
                    setImageBackround(R.drawable.snow)
                in resources.getIntArray(R.array.sleet_id_list) ->
                    setImageBackround(R.drawable.sleet)

                in resources.getIntArray(R.array.clear_id_list) -> {
                    when (icon) {
                        "01d" -> setImageBackround(R.drawable.clear)
                        "01n" -> setImageBackround(R.drawable.clear_night)
                    }
                }
                in resources.getIntArray(R.array.clouds_id_list) ->
                    setImageBackround(R.drawable.lightcloud)

                in resources.getIntArray(R.array.heavy_clouds_id_list) ->
                    setImageBackround(R.drawable.heavycloud)

                in resources.getIntArray(R.array.fog_id_list) ->
                    setImageBackround(R.drawable.fog)

                in resources.getIntArray(R.array.sand_id_list) ->
                    setImageBackround(R.drawable.sand)

                in resources.getIntArray(R.array.dust_id_list) ->
                    setImageBackround(R.drawable.dust)

                in resources.getIntArray(R.array.volcanic_ash_id_list) ->
                    setImageBackround(R.drawable.volcanic)

                in resources.getIntArray(R.array.squalls_id_list) ->
                    setImageBackround(R.drawable.squalls)

                in resources.getIntArray(R.array.tornado_id_list) ->
                    setImageBackround(R.drawable.tornado)
            }
        }
    }

    private fun setImageBackround(image: Int) {
        Glide.with(this).load(image).into(binding.imgBgWeather)
    }

    private fun getWeatherByCurrentLocation() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1000
            )
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                try {
                    val lat = it.latitude
                    val lon = it.longitude

                    viewModel.weatherByCurrentLocation(lat, lon)
                    viewModel.forecastByCurrentLocation(lat, lon)

                } catch (e: Throwable) {
                    Log.i("MainActivity", "Lastlocation coordinate: $it")
                    Log.e("MainActivity", "Couldn't get latitude & longitude")
                }

            }
            .addOnFailureListener {
                Log.e("MainActivity", "Failed getting current location")
            }
        viewModel.weatherByCurrentLocation(0.1, 0.2)
        viewModel.forecastByCurrentLocation(0.2, 0.1)

        viewModel.getWeatherByCity().observe(this) {
            binding.apply {
                tvCity.text = it.name
                tvDegree.text = formatterDegree(it.main?.temp)

                val iconId = it.weather?.get(0)?.icon
                val iconUrl = BuildConfig.ICON_URL + iconId + sizeIconWeather2x
                Glide.with(this@MainActivity).load(iconUrl)
                    .into(imgIcWeather)
            }
        }
        viewModel.getForecastWeatherByCity().observe(this) {
            mAdapter.setData(it.list)
            binding.rvWeather.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(
                    this.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
        }
    }

    private fun searchCity() {
        binding.edtSearch.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        isLoading = true
                        loadingStateView()
                        try {
                            val inputMethodManager =
                                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                        } catch (e: Throwable) {
                            Log.e("MainActivity", e.toString())
                        }
                        viewModel.weatherByCity(it)

                        viewModel.forecastByCity(it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            }
        )

    }

    private fun loadingStateView() {
        binding.apply {
            when (isLoading) {
                true -> {
                    layoutWeather.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                }
                false -> {
                    layoutWeather.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
                else -> {
                    layoutWeather.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }
}