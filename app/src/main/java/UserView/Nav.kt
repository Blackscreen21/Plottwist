package UserView

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plottwist.MyBooksScreen
import com.example.plottwist.SearchScreen

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object MyBooks : Screen("my_books")
}

@Composable
fun Nav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToMyBooks = { navController.navigate(Screen.MyBooks.route) }
            )
        }
        composable(Screen.MyBooks.route) {
            MyBooksScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}