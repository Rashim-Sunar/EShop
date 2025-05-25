package com.example.easyshop

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.easyshop.pages.HomePage
import androidx.navigation.compose.composable
import com.example.easyshop.pages.CartPage
import com.example.easyshop.pages.FavouritePage
import com.example.easyshop.pages.ProfilePage


@Composable
fun BottomNavGraph(
    parentNavController: NavHostController,
    navController: NavHostController,
    paddingValues: PaddingValues
){
    NavHost(
        navController = navController,
        startDestination = "homePage",
        modifier = Modifier.padding(paddingValues) // optional modifier if needed
    ) {
        composable(route = "homePage") { HomePage(parentNavController) }
        composable(route = "favouritePage") { FavouritePage() }
        composable(route = "cartPage") { CartPage() }
        composable(route = "profilePage") { ProfilePage() }
    }



}

