package nick.mirosh.newsapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nick.mirosh.newsapp.ui.details.DetailsScreenContent
import nick.mirosh.newsapp.ui.favorite_articles.FavoriteArticlesScreenContent
import nick.mirosh.newsapp.ui.favorite_articles.FavoriteArticlesViewModel
import nick.mirosh.newsapp.ui.feed.FeedScreen
import nick.mirosh.newsapp.ui.feed.FeedViewModel
import nick.mirosh.newsapp.ui.theme.NewsAppTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    BottomBar(navController = navController)
                }) { paddingValues ->
                    BottomNavGraph(
                        paddingValues = paddingValues,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val screens = listOf(
        BottomBarItem.Home,
        BottomBarItem.Favorites
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    BottomNavigation {
        screens.forEach {
            AddItem(
                screen = it,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}


@Composable
fun RowScope.AddItem(
    screen: BottomBarItem,
    currentDestination: NavDestination?,
    navController: NavController
) {
    BottomNavigationItem(
        label = {
            Text(text = screen.title, color = Color.White)
        },
        selected = currentDestination?.route == screen.route,
        onClick = {
            if (navController.currentDestination?.route != screen.route) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }
        },
        selectedContentColor = Color.Black,
        unselectedContentColor = Color.White,
        icon = {
            Icon(
                imageVector = screen.icon,
                tint = Color.White,
                contentDescription = "Navigation Icon"
            )
        })
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

@Composable
fun BottomNavGraph(paddingValues: PaddingValues, navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomBarItem.Home.route) {
        composable(route = FeedDestination.route) {
            Log.d(TAG, "BottomNavGraph: MainScreenContent() called")
            FeedScreen(
                viewModel = hiltViewModel<FeedViewModel>(),
                onClick = {
                    val encodedUrl =
                        URLEncoder.encode(it.url, StandardCharsets.UTF_8.toString())
                    navController.navigateSingleTopTo("${DetailsDestination.route}/$encodedUrl")

                }
            )
        }
        composable(route = FavoritesDestination.route) {
            FavoriteArticlesScreenContent(
                modifier = Modifier.padding(paddingValues),
                viewModel = hiltViewModel<FavoriteArticlesViewModel>()
            )
        }
        composable(
            route = DetailsDestination.routeWithArgs,
            arguments = DetailsDestination.arguments
        ) {
            val articleUrl =
                it.arguments?.getString(DetailsDestination.articleArg)

            DetailsScreenContent(articleUrl = articleUrl.orEmpty())
        }
    }
}

sealed class BottomBarItem(var title: String, var icon: ImageVector, var route: String) {
    data object Home : BottomBarItem("Home", Icons.Default.Home, FeedDestination.route)
    data object Favorites :
        BottomBarItem("Favorites", Icons.Default.Favorite, FavoritesDestination.route)
}

@Preview
@Composable
fun PreviewFeedScreen() {
    NewsAppTheme {

    }
}

