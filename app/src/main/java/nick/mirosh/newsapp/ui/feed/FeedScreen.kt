package nick.mirosh.newsapp.ui.feed

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import nick.mirosh.newsapp.domain.model.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter


private const val TAG = "MainScreenContent"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
    onClick: (Article) -> Unit
) {
    Log.d(TAG, "MainScreenContent initialized ")
    val uiState by viewModel.newsFeedUiState.collectAsStateWithLifecycle(
        initialValue = ArticlesUiState.Loading
    )
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is ArticlesUiState.Success ->
                LazyColumn {
                    val articles = (uiState as ArticlesUiState.Success).articles
                    items(articles.size) { index ->
                        Log.d(TAG, "FeedScreen: ${articles[index]}")
                        ArticleItem2(articles[index], onClick, viewModel::onLikeClick)
                    }
                }

            is ArticlesUiState.Error ->
                Box {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Error loading articles",
                    )
                }

            is ArticlesUiState.Loading ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Loading articles...",
                    )
                }

        }

    }
}

@Composable
fun ArticleItem(
    article: Article,
    onArticleClick: (Article) -> Unit,
    onLikeCLick: (Article) -> Unit
) {
    val mContext = LocalContext.current

    Row(
        modifier = Modifier.padding(8.dp, 4.dp, 8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clickable { onArticleClick(article) }
                .height(150.dp)
                .padding(8.dp)
                .width(200.dp)
                .clip(shape = RoundedCornerShape(8.dp)),

            model = article.urlToImage,
            contentDescription = "Translated description of what the image contains"
        )
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(200.dp)
                .height(150.dp)
        ) {
            Text(
                text = article.title,
                lineHeight = 18.sp,
                fontSize = 14.sp

            )
            IconButton(
                onClick = {
                    onLikeCLick(article)

                },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {

                Icon(
                    imageVector = if (article.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (article.liked) Color.Red else Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticleItem2(
    article: Article,
    onArticleClick: (Article) -> Unit,
    onLikeCLick: (Article) -> Unit
) {
    Row(
        modifier = Modifier
            .height(216.dp)
            .padding(8.dp, 4.dp, 8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable { onArticleClick(article) }
                .width(200.dp)
                .height(200.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            model = article.urlToImage,
            contentDescription = "Translated description of what the image contains"
        )
        Box(modifier = Modifier.fillMaxHeight().padding(8.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = article.title,
                lineHeight = 18.sp,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.5f),
                    maxLines = 1,
                    text = article.author.lowercase(),
                    fontSize = 12.sp
                )
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(0.5f)
                        .align(Alignment.CenterVertically),
                    text = getReadableDate(article.publishedAt),
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun getReadableDate(timestamp: String) =
    try {
        val date =
            LocalDate.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
        date.format(DateTimeFormatter.ofPattern("dd MMMM"))
    } catch (e: Exception) {
        Log.e(TAG, "getReadableDate: ", e)
        ""
    }

@Preview
@Composable
fun ArticleItemPreview() {
    val article = Article(
        author = "some dude with a really long name",
        content = "content",
        description = "description",
        publishedAt = "2024-02-26T17:17:00Z",
        title = "title",
        url = "url",
        urlToImage = "urlToImage",
        liked = false
    )
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.White)
    ) {
        ArticleItem2(article, {}, {})
    }
}