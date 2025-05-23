package com.example.easyshop.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.AppUtil
import com.example.easyshop.viewmodel.AuthViewModel

@Composable
fun Signup(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
               Column {
                   Text(
                       text = "Hello there!",
                       style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
                       fontWeight = FontWeight.Bold,
                       textAlign = TextAlign.Start,
                       modifier = Modifier.fillMaxWidth()
                   )

                   Text(
                       text = "Create your account",
                       style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                       textAlign = TextAlign.Start,
                       modifier = Modifier.fillMaxWidth()
                   )
               }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("Enter your name") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(0.dp))

                Button(
                    onClick = {
                        isLoading = true
                        authViewModel.signup(name, email, password){ success, errorMessage ->
                            if(success){
                                isLoading = false
                                navController.navigate("home"){
                                    popUpTo("auth"){inclusive = true} // popup all the routes including auth
                                }

                            }else{
                                isLoading = false
                                AppUtil.showToast(context, errorMessage?:"Something went wrong!")
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if(isLoading) "Creating account" else "Signup")
                }

                Column(

                ) {
                    Text(
                        text = "Already have an account. Go to login.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedButton(
                        onClick = {
                            navController.navigate("login"){
                                popUpTo("auth") { inclusive = false } //This clears everything after auth
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp).background(Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SignupPreview(){
    val navController = rememberNavController()
    Signup(navController)
}