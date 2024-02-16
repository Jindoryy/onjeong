package com.a503.onjeong.domain.weather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.weather.api.WeatherApiService
import com.a503.onjeong.domain.weather.dto.WeatherRequestDto
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto
import com.a503.onjeong.global.network.RetrofitClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.util.Locale


class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        findViewById<TextView>(R.id.mainText).text = "날씨"

        val retrofit = RetrofitClient.getApiClient(this)
        val service = retrofit.create(WeatherApiService::class.java)

        val currentAddress = findViewById<TextView>(R.id.address)
        val currentImage = findViewById<ImageView>(R.id.currentImage)
        val currentTemperature = findViewById<TextView>(R.id.currentTemperature)
        val currentSky = findViewById<TextView>(R.id.currentSky)

        val oneImage = findViewById<ImageView>(R.id.oneImage)
        val twoImage = findViewById<ImageView>(R.id.twoImage)
        val threeImage = findViewById<ImageView>(R.id.threeImage)
        val fourImage = findViewById<ImageView>(R.id.fourImage)
        val fiveImage = findViewById<ImageView>(R.id.fiveImage)
        val sixImage = findViewById<ImageView>(R.id.sixImage)
        val sevenImage = findViewById<ImageView>(R.id.sevenImage)

        val imageViews: List<ImageView> =
            listOf(oneImage, twoImage, threeImage, fourImage, fiveImage, sixImage, sevenImage)

        val oneWeek = findViewById<TextView>(R.id.oneWeek)
        val twoWeek = findViewById<TextView>(R.id.twoWeek)
        val threeWeek = findViewById<TextView>(R.id.threeWeek)
        val fourWeek = findViewById<TextView>(R.id.fourWeek)
        val fiveWeek = findViewById<TextView>(R.id.fiveWeek)
        val sixWeek = findViewById<TextView>(R.id.sixWeek)
        val sevenWeek = findViewById<TextView>(R.id.sevenWeek)

        val weekTextViews: List<TextView> =
            listOf(oneWeek, twoWeek, threeWeek, fourWeek, fiveWeek, sixWeek, sevenWeek)

        val oneTemperature = findViewById<TextView>(R.id.oneTemperature)
        val twoTemperature = findViewById<TextView>(R.id.twoTemperature)
        val threeTemperature = findViewById<TextView>(R.id.threeTemperature)
        val fourTemperature = findViewById<TextView>(R.id.fourTemperature)
        val fiveTemperature = findViewById<TextView>(R.id.fiveTemperature)
        val sixTemperature = findViewById<TextView>(R.id.sixTemperature)
        val sevenTemperature = findViewById<TextView>(R.id.sevenTemperature)

        val temperatureTextViews: List<TextView> =
            listOf(
                oneTemperature,
                twoTemperature,
                threeTemperature,
                fourTemperature,
                fiveTemperature,
                sixTemperature,
                sevenTemperature
            )

// 홈버튼 누르면 홈으로 이동하게
        val homeButton : Button = findViewById(R.id.btnHome)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 뒤로가기 버튼 누르면 뒤로(메인)이동
        val backButton : Button = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        getLocation(object : LocationCallback {
            override fun onLocationReceived(address: List<Address>) {
                // 위치를 받은 후 API 요청 수행
                var call: Call<List<WeatherResponseDto>>? = null

                var idx = 0
                var cnt = 0;
                var l = ""

                for (a in address) {
                    var add = a.getAddressLine(0).split(" ")
                    if (add.get(add.size - 1).last().equals('동') ||
                        add.get(add.size - 1).last().equals('읍') ||
                        add.get(add.size - 1).last().equals('면')
                    ) {
                        if (l.length < add.get(add.size - 1).length) {
                            idx = cnt
                            l = add.get(add.size - 1)
                        }
                    }
                    cnt += 1
                }

                var splitAddr = address.get(idx).getAddressLine(0).split(" ")

                if (splitAddr.size == 4) {
                    call = service.getWeatherInfo(
                        WeatherRequestDto(
                            splitAddr[1],
                            "",
                            splitAddr[2],
                            splitAddr[3]
                        )
                    )
                    currentAddress.text = splitAddr[1] + " " + splitAddr[2] + " " + splitAddr[3]
                } else {
                    call = service.getWeatherInfo(
                        WeatherRequestDto(
                            splitAddr[1],
                            splitAddr[2],
                            splitAddr[3],
                            splitAddr[4]
                        )
                    )
                    currentAddress.text =
                        splitAddr[1] + " " + splitAddr[2] + " " + splitAddr[3] + " " + splitAddr[4]
                }


                call.enqueue(object : Callback<List<WeatherResponseDto>> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(
                        call: Call<List<WeatherResponseDto>>,
                        response: Response<List<WeatherResponseDto>>
                    ) {
                        // API 응답 처리
                        if (response.isSuccessful) {
                            val weatherResponseDto: List<WeatherResponseDto>? = response.body()
                            var now = LocalDate.now()

                            if (weatherResponseDto != null) {
                                for (index in weatherResponseDto.indices) {
                                    val weatherDto = weatherResponseDto[index]
                                    val temperatures = weatherDto.temperatures
                                    val temperaturesLow = weatherDto.temperaturesLow
                                    val temperaturesHigh = weatherDto.temperaturesHigh
                                    val sky = weatherDto.sky
                                    val pty = weatherDto.pty

                                    val image = imageViews[index]
                                    val weekText = weekTextViews[index]
                                    val temperatureText = temperatureTextViews[index]

                                    if (index == 0) {
                                        currentImage.setImageResource(getImage(sky, pty))
                                        currentTemperature.text =
                                            temperatures.toInt().toString() + "°C"
                                        currentSky.text = getSkyStatus(sky, pty)
                                    }

                                    image.setImageResource(getImage(sky, pty))
                                    weekText.text =
                                        "  " + now.monthValue.toString() + "월 " + now.dayOfMonth.toString() + "일"
                                    temperatureText.text =
                                        temperaturesLow.toInt()
                                            .toString() + "°C / " + temperaturesHigh.toInt()
                                            .toString() + "°C"
                                    now = now.plusDays(1)
                                }
                            }

                        } else {
                            Log.e("Weather Error", "지역 오류")
                        }
                    }

                    override fun onFailure(call: Call<List<WeatherResponseDto>>, t: Throwable) {
                        Log.e("Weather Error", "Request failed: ${t.message}")
                    }
                })
            }
        })

    }

    @SuppressLint("MissingPermission")
    private fun getLocation(locationCallback: LocationCallback) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    val address: List<Address>? = getAddress(location.latitude, location.longitude)
                    if (address != null) {
                        locationCallback.onLocationReceived(address)
                    }
                }
            }
    }

    interface LocationCallback {
        fun onLocationReceived(address: List<Address>)
    }

    private fun getAddress(lat: Double, lng: Double): List<Address>? {
        lateinit var address: List<Address>

        return try {
            val geocoder = Geocoder(this, Locale.KOREA)
            address = geocoder.getFromLocation(lat, lng, 10) as List<Address>
            address
        } catch (e: IOException) {
            Toast.makeText(this, "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun getImage(sky: Int, pty: Int): Int {
        if (pty > 0) {
            if (pty == 1) return R.drawable.rain
            else if (pty == 2) return R.drawable.snow_rain
            else if (pty == 3) return R.drawable.snow
            else return R.drawable.rain
        } else {
            if (sky == 1) return R.drawable.sunny
            else if (sky == 3) return R.drawable.large_cloud
            else return R.drawable.blur
        }
    }

    private fun getSkyStatus(sky: Int, pty: Int): String {
        if (pty > 0) {
            if (pty == 1) return "비"
            else if (pty == 2) return "눈/비"
            else if (pty == 3) return "눈"
            else return "소나기"
        } else {
            if (sky == 1) return "맑음"
            else if (sky == 3) return "구름많음"
            else return "흐림"
        }
    }

}