package nick.mirosh.newsapp.data.repository

import kotlinx.coroutines.flow.Flow
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.Result

interface NewsRepository {
    suspend fun getNews(country: String): Flow<Result<List<Article>>>

    suspend fun getFavoriteArticles(): Flow<Result<List<Article>>>

    suspend fun updateArticle(article: Article): Flow<Result<Unit>>
}