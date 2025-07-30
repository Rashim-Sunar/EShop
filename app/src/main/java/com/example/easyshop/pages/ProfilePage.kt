package com.example.easyshop.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easyshop.GlobalNavController
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun ProfilePage(navController: NavController){

    val currentUser = remember { mutableStateOf<UserModel?>(null) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    currentUser.value = snapshot.toObject(UserModel::class.java)
                }else{
                    Log.e("ProfilePage", "Snapshot not found")
                }
            }
            .addOnFailureListener { err ->
                Log.e("ProfilePage", "Failed to fetch user data store")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "My Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = currentUser.value?.name ?: "User" , fontWeight = FontWeight.Bold)
                Text(text = currentUser.value?.email ?: "rashim@example.com", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options List
        AccountOptionItem("My Orders", Icons.Default.ShoppingCart, "ordersPage")
        AccountOptionItem("Addresses", Icons.Default.LocationOn)
        AccountOptionItem("Payment Methods", Icons.Default.Payment)
        AccountOptionItem("Settings", Icons.Default.Settings)
        AccountOptionItem("Help & Support", Icons.Default.Help)

        Spacer(modifier = Modifier.height(22.dp))

        // --------------- Logout Button ----------------------//
        Card(
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(
                    onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("auth"){
                            popUpTo("home") {inclusive = true }
                        }
                    }
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(Color.White)
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Exit icon",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Log Out", fontSize = 15.sp, color = Color.Blue)
            }
        }
    }
}

@Composable
fun AccountOptionItem(title: String, icon: ImageVector, route: String = "homePage", iconTint: Color = Color.Blue) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable{
                GlobalNavController.navController.navigate(route)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp)
        }
    }
}

