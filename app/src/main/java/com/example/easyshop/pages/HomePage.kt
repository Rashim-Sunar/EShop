package com.example.easyshop.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.easyshop.components.BannerView
import com.example.easyshop.components.HeaderView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(){
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
    ) {
        HeaderView()
        Spacer(modifier = Modifier.height(10.dp))
        BannerView()
    }
}