package nick.mirosh.newsapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nick.mirosh.newsapp.data.repository.NewsRepository
import nick.mirosh.newsapp.di.Universal
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @Universal private val newsRepository: NewsRepository,
) : ViewModel() {

    private val _articles = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val articles = _articles.asStateFlow<ArticlesUiState>()

    init {
        viewModelScope.launch {
            newsRepository.refreshNews().collect {
                _articles.value = when (it) {
                    is Result.Success ->
                        ArticlesUiState.Success(it.data)

                    is Result.Failure ->
                        ArticlesUiState.Error
                }
            }
        }
    }

    fun onLikeClick(article: Article) {
        viewModelScope.launch {
            newsRepository.updateArticle(article.copy(liked = !article.liked))
        }
    }
}

sealed interface ArticlesUiState {
    data object Loading : ArticlesUiState
    data object Error : ArticlesUiState
    data class Success(val articles: List<Article>) : ArticlesUiState
}