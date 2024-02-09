package nick.mirosh.newsapp.data.repository

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
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDataSource: NewsRemoteDataSource? = null,
    private val articleDao: ArticleDao
) : NewsRepository {

    override suspend fun refreshNews(): Flow<Result<List<Article>>> = flow<Result<List<Article>>> {
        try {
            val articles = newsDataSource?.getHeadlines() ?: emptyList()
            if (articles.isNotEmpty()) {
                articleDao.insertAll(articles.map {
                    it.asDatabaseArticle()
                })
            }
        } finally {
            emit(Result.Success(articleDao.getAllArticles().map {
                it.asDomainModel()
            }))
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