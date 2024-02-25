package nick.mirosh.newsapp.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nick.mirosh.newsapp.domain.Result
import nick.mirosh.newsapp.domain.model.Article
import nick.mirosh.newsapp.domain.usecase.GetArticlesUseCase
import nick.mirosh.newsapp.domain.usecase.UpdateArticleUseCase
import javax.inject.Inject


private const val TAG = "MainViewModel"
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
) : ViewModel() {

//    private val _newsFeedUiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
//    val newsFeedUiState = _newsFeedUiState.asStateFlow()

    val newsFeedUiState = getArticlesUseCase("us").map { result ->
        when (result) {
            is Result.Success -> {
                val filteredArticles = result.data.filterNot {
                    it.urlToImage.isEmpty()
                }
                ArticlesUiState.Success(filteredArticles)
            }

            is Result.Failure ->
                ArticlesUiState.Error
        }
    }

    init {
        Log.d(TAG, "init() called")
    }
//    val newsFeedUiState = _newsFeedUiState.asStateFlow()

//    init {
//        //TODO don't reload this every time we land on the page
//        viewModelScope.launch {
//            getArticlesUseCase("us").collect { result ->
//                _newsFeedUiState.value = when (result) {
//                    is Result.Success -> {
//                        val filteredArticles = result.data.filterNot {
//                            it.urlToImage.isEmpty()
//                        }
//                        ArticlesUiState.Success(filteredArticles)
//                    }
//
//                    is Result.Failure ->
//                        ArticlesUiState.Error
//                }
//            }
//        }
//    }


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