package nick.mirosh.newsapp.ui

import android.os.Bundle
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
import androidx.compose.material3.LocalContentColor
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ContentAlpha
import dagger.hilt.android.AndroidEntryPoint
import nick.mirosh.newsapp.ui.favorite_articles.FavoriteArticlesScreenContent
import nick.mirosh.newsapp.ui.favorite_articles.FavoriteArticlesViewModel
import nick.mirosh.newsapp.ui.feed.MainScreenContent
import nick.mirosh.newsapp.ui.feed.MainViewModel
import nick.mirosh.newsapp.ui.theme.NewsAppTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppTheme {
                NewsFeedScreen()
            }
        }
    }
}

@Composable
fun NewsFeedScreen() {
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

sealed class BottomBarItem(var title: String, var icon: ImageVector, var route: String) {

    object Home : BottomBarItem("Home", Icons.Default.Home, "home")
    object Favorites : BottomBarItem("Favorites", Icons.Default.Favorite, "favorites")
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
                navController.navigate(screen.route)
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
        composable(route = BottomBarItem.Home.route) {
            val viewModel = hiltViewModel<MainViewModel>()
            MainScreenContent(
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel,
                onClick = {
                    val encodedUrl =
                        URLEncoder.encode(it.url, StandardCharsets.UTF_8.toString())

                    navController.navigateSingleTopTo("${Details.route}/$encodedUrl")

                },
                onSavedArticlesClicked = {
                    navController.navigateSingleTopTo(FavoriteArticles.route)
                })
        }
        composable(route = BottomBarItem.Favorites.route) {
            val viewModel = hiltViewModel<FavoriteArticlesViewModel>()
            FavoriteArticlesScreenContent(
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel
            )
        }
    }
}

@Preview
@Composable
fun PreviewFeedScreen() {
    NewsAppTheme {
        NewsFeedScreen()
    }
}

