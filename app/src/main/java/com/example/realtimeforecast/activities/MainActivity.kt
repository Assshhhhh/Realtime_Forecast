package com.example.realtimeforecast.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.realtimeforecast.api_interface.ApiInterface
import com.example.realtimeforecast.R
import com.example.realtimeforecast.models.WeatherApp
import com.example.realtimeforecast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val apiKey = "eec74950662a4a016e7a511dca9a644b"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        fetchWeatherData("Bhakkar")
        searchCity()
    }

    private fun searchCity(){

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherData(cityName: String) {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, apiKey, "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){

                    //val city = searchCity()
                    val temperature = responseBody.main.temp
                    val max_temp = responseBody.main.temp_max
                    val min_temp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val day = dayName(System.currentTimeMillis())
                    val date = date()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure

                    binding.tvCity.text = "$cityName"
                    binding.tvTemp.text = "$temperature °C"
                    binding.tvMaxTemp.text = "Max Temp: $max_temp °C"
                    binding.tvMinTemp.text = "Min Temp: $min_temp °C"
                    binding.tvWeather.text = "$condition"
                    binding.tvDay.text = "$day"
                    binding.tvDate.text = "$date"
                    binding.tvHumidity.text = "$humidity %"
                    binding.tvWindspeed.text = "$windSpeed m/s"
                    binding.tvCondition.text = "$condition"
                    binding.tvSunrise.text = "${time(sunrise)}"
                    binding.tvSunset.text = "${time(sunset)}"
                    binding.tvSea.text = "$seaLevel hPa"

                    changeBgWithCondition(condition)

                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeBgWithCondition(condition: String) {

        when(condition){
            "Clear", "Clear Sky", "Sunny", "Sun" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Fog", "Smoke", "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animation.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain", "Rainy" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animation.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animation.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sun)
            }
        }
        binding.animation.playAnimation()

    }

    private fun date(): String {

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }

    private fun time(timeStamp: Long): String {

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))

    }

    private fun dayName(timeStamp: Long): String {

        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }
}