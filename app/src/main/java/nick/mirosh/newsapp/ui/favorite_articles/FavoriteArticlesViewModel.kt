package nick.mirosh.newsapp.ui.favorite_articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nick.mirosh.newsapp.di.Cache
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

@HiltViewModel
class FavoriteArticlesViewModel @Inject constructor(
    @Cache private val newsRepository: NewsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoriteArticlesUIState>(FavoriteArticlesUIState.Empty)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            newsRepository.getFavoriteArticles().collect { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data.isEmpty())
                            FavoriteArticlesUIState.Empty
                        else
                            FavoriteArticlesUIState.Success(result.data)
                    }

                    is Result.Failure ->
                        FavoriteArticlesUIState.Empty
                }
            }
        }
    }
}
sealed interface FavoriteArticlesUIState {
    data object Empty : FavoriteArticlesUIState
    data class Success(val articles: List<Article>) : FavoriteArticlesUIState
}
