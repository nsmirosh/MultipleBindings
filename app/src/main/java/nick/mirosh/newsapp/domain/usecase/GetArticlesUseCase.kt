package nick.mirosh.newsapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import nick.mirosh.newsapp.di.Universal
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    @Universal private val newsRepository: NewsRepository,
) {
    operator fun invoke(country: String): Flow<Result<List<Article>>> = newsRepository.getNews(country)

}