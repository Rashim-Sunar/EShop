package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun login(){

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