package com.example.easyshop.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.R

@Composable
fun AuthScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.epicanime),
            contentDescription = "Banner image",
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Start your shopping journey now.",
            style = TextStyle(
                fontSize = 27.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Best ecom platform with best prices.",
            style = TextStyle(
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = {navController.navigate("login")}
        ){
            Text(text = "Login", fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = {navController.navigate("signup")}
        ){
            Text(text = "Signup", fontSize = 17.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    val navController = rememberNavController()
    AuthScreen(navController)
}
