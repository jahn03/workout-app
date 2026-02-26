package com.example.workoutapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var selectedMode by remember { mutableStateOf("coach") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "What is your primary purpose?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            RadioButton(
                selected = selectedMode == "coach",
                onClick = { selectedMode = "coach" }
            )
            Text("Train Clients")
        }

        Row {
            RadioButton(
                selected = selectedMode == "personal",
                onClick = { selectedMode = "personal" }
            )
            Text("Track My Own Workouts")
        }

        Row {
            RadioButton(
                selected = selectedMode == "both",
                onClick = { selectedMode = "both" }
            )
            Text("Both")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                val uid = auth.currentUser?.uid ?: return@Button

                isSaving = true

                val enabledModes = when (selectedMode) {
                    "coach" -> listOf("coach", "personal")
                    "personal" -> listOf("personal")
                    "both" -> listOf("coach", "personal")
                    else -> listOf("personal")
                }

                val userProfile = hashMapOf(
                    "primaryMode" to selectedMode,
                    "enabledModes" to enabledModes
                )

                firestore.collection("users")
                    .document(uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        isSaving = false
                        onComplete()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text("Continue")
        }
    }
}