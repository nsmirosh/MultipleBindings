package nick.mirosh.newsapp.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nick.mirosh.newsapp.data.database.ArticleDao
import nick.mirosh.newsapp.data.model.asDatabaseArticle
import nick.mirosh.newsapp.data.model.asDomainModel
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.model.asDatabaseModel
import java.io.IOException
import javax.inject.Inject

private const val TAG = "NewsRepositoryImpl"

class NewsRepositoryImpl @Inject constructor(
    private val newsDataSource: NewsRemoteDataSource? = null,
    private val articleDao: ArticleDao
) : NewsRepository {

    override suspend fun getNews(country: String): Flow<Result<List<Article>>> = flow {
        try {
            //implement polling every 10 seconds here
            val articles = newsDataSource?.getHeadlines(country) ?: emptyList()
            articleDao.insertAll(articles.map {
                it.asDatabaseArticle()
            })
        } catch (e: IOException) {
            Log.d(TAG, "getNews: ${e.message}")
        } finally {
            articleDao.getAllArticles().also { databaseArticles ->
                if (databaseArticles.isNotEmpty()) {
                    val articles = databaseArticles.map {
                        it.asDomainModel()
                    }
                    emit(Result.Success(articles))
                } else {
                    emit(Result.Failure("No articles found"))
                }
            }

        }
    }
        .flowOn(
            Dispatchers.IO
        )

    override suspend fun getFavoriteArticles(): Flow<Result<List<Article>>> = flow {
        try {
            emit(
                Result.Success(
                    articleDao.getLikedArticles().map {
                        it.asDomainModel()
                    }
                )
            )
        } catch (e: Exception) {
            emit(Result.Failure(e.message))
        }
    }.flowOn(
        Dispatchers.IO
    )

    override suspend fun updateArticle(article: Article): Flow<Result<Unit>> = flow {
        try {
            val dbArticle = article.asDatabaseModel()
            emit(Result.Success(articleDao.insert(dbArticle)))
        } catch (
            e: Exception
        ) {
            emit(Result.Failure(e.message))
        }
    }.flowOn(Dispatchers.IO)
}