package com.example.easyshop.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.components.CartItemView
import com.example.easyshop.model.ProductsModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(){

    val userModel = remember{
        mutableStateOf(UserModel())
    }

    LaunchedEffect(Unit) {
        val userId: String = Firebase.auth.currentUser?.uid  ?: return@LaunchedEffect

        Firebase.firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null){
                    Log.w("CartItemView", "Error fetching product details", error)
                    return@addSnapshotListener
                }

                if (snapshot.exists()) {
                   val result = snapshot.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                } else {
                    Log.d("CartPage", "Products not found or null snapshot.")
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Text(
            text = "Cart Items",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(userModel.value.cartItems.toList(), key = {it.first}){(productId, quantity) ->
                CartItemView(productId, quantity)
            }
        }
    }
}