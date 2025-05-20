package com.example.easyshop

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.screen.AuthScreen
import com.example.easyshop.screen.Login
import com.example.easyshop.screen.Signup

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth"){

        composable(route = "auth"){
            AuthScreen(navController = navController)
        }

        composable(route = "login") {
            Login(navController)

        }

        composable(route = "signup") {
            Signup(navController)
        }
    }
}

