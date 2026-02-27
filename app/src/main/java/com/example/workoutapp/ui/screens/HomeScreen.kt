package com.example.workoutapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.workoutapp.ui.model.Workout


@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onAddWorkoutClick: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var primaryMode by remember { mutableStateOf<String?>(null) }
    var enabledModes by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeMode by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var workouts by remember { mutableStateOf<List<Workout>>(emptyList()) }

    // Load profile once
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

            firestore.collection("users")
                .document(uid)
                .collection("workouts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        workouts = snapshot.documents.map {
                            Workout(
                                id = it.id,
                                name = it.getString("name") ?: "",
                                notes = it.getString("notes") ?: ""
                            )
                        }
                    }
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

        // Top Bar
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
                auth.signOut()
                onLogout()
            }) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mode Selector
        if (enabledModes.isNotEmpty()) {

            Text("Active Mode:")
            Spacer(modifier = Modifier.height(8.dp))

            enabledModes.forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {

                    RadioButton(
                        selected = activeMode == mode,
                        onClick = { activeMode = mode }
                    )

                    Text(mode.replaceFirstChar { it.uppercase() })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mode-Specific Content
        when (activeMode) {

            "coach" -> {
                Text(
                    text = "Coach Dashboard",
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

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { onAddWorkoutClick() }) {
                    Text("Add Workout")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "My Workouts",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (workouts.isEmpty()) {
                    Text("No workouts yet.")
                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(workouts) { workout ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = workout.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    if (workout.notes.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(workout.notes)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                Text("No mode available.")
            }
        }
    }
}