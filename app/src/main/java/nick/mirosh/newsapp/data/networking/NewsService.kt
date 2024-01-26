package nick.mirosh.newsapp.data.networking

import nick.mirosh.newsapp.data.model.ApiResponse
import nick.mirosh.newsapp.data.model.NetworkArticle
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsService {

    @GET("top-headlines")
    fun getHeadlines(@Query("country") country: String): Call<ApiResponse<NetworkArticle>>

}