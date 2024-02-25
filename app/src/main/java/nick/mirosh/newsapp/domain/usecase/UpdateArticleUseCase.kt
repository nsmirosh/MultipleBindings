package nick.mirosh.newsapp.domain.usecase

import nick.mirosh.newsapp.di.Universal
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class UpdateArticleUseCase @Inject constructor(
    @Universal private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(article: Article) = newsRepository.updateArticle(article)

}