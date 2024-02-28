package nick.mirosh.newsapp.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.usecase.GetArticlesUseCase
import nick.mirosh.newsapp.domain.usecase.UpdateArticleUseCase
import javax.inject.Inject


private const val TAG = "MainViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
) : ViewModel() {
    // MutableStateFlow to hold the current country code
    private val _countryCode = MutableStateFlow("us")
    //TODO for the search functionality - https://medium.com/@Nathan_Sass/designing-a-type-ahead-for-android-with-coroutines-ffe5c00c5cf7

    // Expose an immutable version if needed
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val newsFeedUiState: StateFlow<ArticlesUiState> = _countryCode.flatMapLatest { country ->
        getArticlesUseCase(country).map { result ->
            when (result) {
                is Result.Success -> {
                    val filteredArticles = result.data
                        .filter {
                            it.urlToImage.isNotEmpty()
                        }.sortedByDescending {
                            it.publishedAt
                        }
                    ArticlesUiState.Success(filteredArticles)
                }

                is Result.Failure ->
                    ArticlesUiState.Error
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ArticlesUiState.Loading
    )

    fun updateCountryCode(newCountryCode: String) {
        _countryCode.value = newCountryCode
    }

    fun onLikeClick(article: Article) {
        viewModelScope.launch {
            updateArticleUseCase(article.copy(liked = !article.liked))
        }
    }
}

sealed interface ArticlesUiState {
    data object Loading : ArticlesUiState
    data object Error : ArticlesUiState
    data class Success(val articles: List<Article>) : ArticlesUiState
}