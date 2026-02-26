package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.workoutapp.ui.theme.WorkoutAppTheme
import com.example.workoutapp.ui.screens.AuthScreen
import com.example.workoutapp.ui.screens.OnboardingScreen
import com.example.workoutapp.ui.screens.HomeScreen
import com.example.workoutapp.ui.screens.AddWorkoutScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class AppScreen {
    LOADING,
    AUTH,
    ONBOARDING,
    HOME,
    ADD_WORKOUT
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

                // Auto-check on launch
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
                            },
                            onAddWorkoutClick = {
                                currentScreen = AppScreen.ADD_WORKOUT
                            }
                        )
                    }

                    AppScreen.ADD_WORKOUT -> {
                        AddWorkoutScreen(
                            onSaveComplete = {
                                currentScreen = AppScreen.HOME
                            },
                            onCancel = {
                                currentScreen = AppScreen.HOME
                            }
                        )
                    }
                }
            }
        }
    }
}