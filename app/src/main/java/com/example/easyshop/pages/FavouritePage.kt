package com.example.easyshop.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easyshop.components.FavouriteItemView
import com.example.easyshop.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FavouritePage() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var favorites by remember { mutableStateOf<List<String>>(emptyList()) }

    // Listen for updates using addSnapshotListener
    DisposableEffect(uid) {
        if (uid != null) {
            val listenerRegistration = db.collection("users").document(uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("FAV", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(UserModel::class.java)
                        favorites = user?.favorites ?: emptyList()
                    }
                }

            // Cleanup listener on dispose
            onDispose {
                listenerRegistration.remove()
            }
        } else {
            onDispose { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp, horizontal = 6.dp)
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Text("No favorites yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(favorites, key = {it}) { fav ->
                    FavouriteItemView(fav)
                }
            }
        }
    }
}
