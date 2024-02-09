package nick.mirosh.newsapp.domain.repository

import kotlinx.coroutines.flow.Flow
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.Result

interface NewsRepository {
    suspend fun refreshNews(): Flow<Result<List<Article>>>
    suspend fun getFavoriteArticles(): Flow<Result<List<Article>>>
    suspend fun updateArticle(article: Article)
}