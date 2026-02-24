package com.example.workoutapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (isLogin) "Login" else "Sign Up",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (passwordVisible)
                    "Hide Password"
                else
                    "Show Password"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                errorMessage = ""
                isLoading = true

                if (isLogin) {
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener {
                            isLoading = false
                            onLoginSuccess()
                        }
                        .addOnFailureListener {
                            isLoading = false
                            errorMessage = it.message ?: "Login Failed"
                        }
                } else {
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener {
                            isLoading = false
                            onSignUpSuccess()
                        }
                        .addOnFailureListener {
                            isLoading = false
                            errorMessage = it.message ?: "Signup Failed"
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLogin) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                if (isLogin)
                    "Don't have an account? Sign Up"
                else
                    "Already have an account? Login"
            )
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }


    }
}