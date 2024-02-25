package nick.mirosh.newsapp.data.repository

import nick.mirosh.newsapp.data.model.NetworkArticle
import nick.mirosh.newsapp.data.networking.NewsService
import javax.inject.Inject

class NewsRemoteDataSource @Inject constructor(private val newsService: NewsService) {
    fun getHeadlines(country: String): List<NetworkArticle> {
         return newsService.getHeadlines(country).execute().body()!!.articles
    }
}