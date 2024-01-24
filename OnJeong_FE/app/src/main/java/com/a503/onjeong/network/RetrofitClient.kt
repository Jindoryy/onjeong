
import com.example.myapplication.News
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers

interface RetrofitClient {

    @Headers("Content-Type: application/json")
    @GET("/news/lists")
    open fun newsList(): Call<List<News>>


}