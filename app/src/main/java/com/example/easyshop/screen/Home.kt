package com.example.easyshop.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.BottomNavGraph
@Composable
fun Home(parentNavController: NavHostController){

    val bottomNavController = rememberNavController()

    val topLevelRoutes = listOf(
        TopLevelRoute(name = "Home", route = "homePage", icon = Icons.Default.Home),
        TopLevelRoute(name = "Favourites", route = "favouritePage", icon = Icons.Default.Favorite),
        TopLevelRoute(name = "Cart", route = "cartPage", icon = Icons.Default.ShoppingCart),
        TopLevelRoute(name = "Profile", route = "profilePage", icon = Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.route == topLevelRoute.route,
                        onClick = {
                            bottomNavController.navigate(topLevelRoute.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues -> // ✅ Add padding values here
        BottomNavGraph(
            parentNavController = parentNavController,
            navController = bottomNavController,
            paddingValues = paddingValues
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview(){
    val navController = rememberNavController()
    Home(navController)
}

data class TopLevelRoute(val name: String, val route: String, val icon: ImageVector)