package nick.mirosh.newsapp.domain.usecase

import nick.mirosh.newsapp.di.Universal
import nick.mirosh.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    @Universal private val newsRepository: NewsRepository,
) {
    operator fun invoke(country: String) = newsRepository.getNews(country)

}