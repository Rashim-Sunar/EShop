package com.example.easyshop

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.screen.AuthScreen
import com.example.easyshop.screen.Home
import com.example.easyshop.screen.Login
import com.example.easyshop.screen.Signup
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    val currentUser = Firebase.auth.currentUser!=null
    val startingScreen = if(currentUser) "home" else "auth"

    NavHost(navController = navController, startDestination = startingScreen){

        composable(route = "auth"){
            AuthScreen(navController = navController)
        }

        composable(route = "login") {
            Login(navController)
        }

        composable(route = "signup") {
            Signup(navController)
        }

        composable(route = "home") {
            Home(navController)
        }
    }
}

