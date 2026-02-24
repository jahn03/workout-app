package com.example.workoutapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.workoutapp.ui.theme.WorkoutAppTheme
import com.example.workoutapp.ui.screens.AuthScreen
import com.example.workoutapp.ui.screens.OnboardingScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import com.example.workoutapp.ui.screens.HomeScreen


enum class AppScreen {
    LOADING,
    AUTH,
    ONBOARDING,
    HOME
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WorkoutAppTheme {

                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()

                var currentScreen by remember {
                    mutableStateOf(AppScreen.LOADING)
                }

                //  Auto-check when app launches
                LaunchedEffect(Unit) {

                    val currentUser = auth.currentUser

                    if (currentUser == null) {
                        currentScreen = AppScreen.AUTH
                    } else {

                        firestore.collection("users")
                            .document(currentUser.uid)
                            .get()
                            .addOnSuccessListener { document ->

                                if (document.exists()) {
                                    currentScreen = AppScreen.HOME
                                } else {
                                    currentScreen = AppScreen.ONBOARDING
                                }
                            }
                            .addOnFailureListener {
                                currentScreen = AppScreen.AUTH
                            }
                    }
                }

                when (currentScreen) {

                    AppScreen.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    AppScreen.AUTH -> {
                        AuthScreen(
                            onLoginSuccess = {
                                currentScreen = AppScreen.HOME
                            },
                            onSignUpSuccess = {
                                currentScreen = AppScreen.ONBOARDING
                            }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onComplete = {
                                currentScreen = AppScreen.HOME
                            }
                        )
                    }

                    AppScreen.HOME -> {
                        HomeScreen(
                            onLogout = {
                                currentScreen = AppScreen.AUTH
                            }
                        )
                    }

                }
            }
        }
    }
}