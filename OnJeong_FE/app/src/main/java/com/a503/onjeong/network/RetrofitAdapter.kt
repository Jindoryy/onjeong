import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NetRetrofit private constructor() {
    var retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create()).build()
    var service = retrofit.create(RetrofitClient::class.java)

    companion object {
        val instance = NetRetrofit()
    }
}