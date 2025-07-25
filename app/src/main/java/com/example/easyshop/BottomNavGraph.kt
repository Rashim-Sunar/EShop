package com.example.easyshop

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.easyshop.pages.HomePage
import androidx.navigation.compose.composable
import com.example.easyshop.pages.CartPage
import com.example.easyshop.pages.CategoryProductsPage
import com.example.easyshop.pages.FavouritePage
import com.example.easyshop.pages.ProductDetailsPage
import com.example.easyshop.pages.ProfilePage
import com.example.easyshop.pages.CheckoutPage
import com.example.easyshop.screen.OrderSuccessScreen

@Composable
fun BottomNavGraph(
    parentNavController: NavHostController,
    navController: NavHostController,
    paddingValues: PaddingValues
){
    GlobalNavController.navController = navController

    NavHost(
        navController = navController,
        startDestination = "homePage",
        modifier = Modifier.padding(paddingValues) // optional modifier if needed
    ) {
        composable(route = "homePage") { HomePage() }
        composable(route = "favouritePage") { FavouritePage() }
        composable(route = "cartPage") { CartPage() }
        composable(route = "profilePage") { ProfilePage(parentNavController) }
        composable(route = "categoryProducts/{categoryId}") {
            val categoryId = it.arguments?.getString("categoryId")
            CategoryProductsPage(categoryId)
        }

        composable(route = "productDetailsPage/{productId}") {
            val productId = it.arguments?.getString("productId")
            ProductDetailsPage(productId)
        }

        composable("checkout/{productId}/{quantity}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val quantity = backStackEntry.arguments?.getString("quantity")?.toLongOrNull() ?: 1L
            CheckoutPage(productId = productId, quantity = quantity)
        }

        composable("order_success/{amount}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: ""
            OrderSuccessScreen(totalAmount = amount)
        }
    }
}
object GlobalNavController{
    @SuppressLint("StaticFieldLeak")
    lateinit var navController: NavHostController
}

