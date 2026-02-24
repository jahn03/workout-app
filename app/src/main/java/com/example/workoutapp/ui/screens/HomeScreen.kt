package com.example.workoutapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(onLogout: () -> Unit) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var primaryMode by remember { mutableStateOf<String?>(null) }
    var enabledModes by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeMode by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    //  Load profile once
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    primaryMode = document.getString("primaryMode")
                    enabledModes =
                        document.get("enabledModes") as? List<String> ?: emptyList()

                    activeMode = primaryMode
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        //  Top Bar Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Workout App",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //  Mode Selector
        if (enabledModes.isNotEmpty()) {

            Text("Active Mode:")
            Spacer(modifier = Modifier.height(8.dp))

            enabledModes.forEach { mode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = activeMode == mode,
                        onClick = { activeMode = mode }
                    )
                    Text(mode.replaceFirstChar { it.uppercase() })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        //  Render Mode-Specific Content
        when (activeMode) {

            "trainer" -> {
                Text(
                    text = "Trainer Dashboard 🏋️",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Manage clients, create programs, track progress.")
            }

            "personal" -> {
                Text(
                    text = "Personal Workout View 💪",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Log workouts and track your own progress.")
            }

            else -> {
                Text("No mode available.")
            }
        }
    }
}