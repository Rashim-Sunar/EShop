package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun login(email: String, password: String, onResult : (Boolean, String?)-> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // Sign in success, navigate to home screen
                    onResult(true, null)
                }else{
                    // If sign in fails, display a message to the user.
                    onResult(false, "Authentication failed")
                }
            }
    }

    fun signup(name: String, email: String, password: String, onResult : (Boolean, String?)-> Unit){
        // Create a user and add to the firestore
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // Sign in success, add user to the firestore
                    val userId = task.result?.user?.uid
                    val userModel = UserModel(name, email, userId!!)

                    firestore.collection("users").document(userId)
                        .set(userModel)
                        .addOnCompleteListener { dbTask ->
                            if(dbTask.isSuccessful){
                              onResult(true, null)
                            }else{
                                onResult(false, "Something went wrong.")
                            }
                        }

                }else{
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }
}