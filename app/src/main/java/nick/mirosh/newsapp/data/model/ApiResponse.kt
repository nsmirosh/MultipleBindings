package nick.mirosh.newsapp.data.model

data class ApiResponse<T> (
    val status: String,
    val articles: List<T>
)