package com.vapuss.hikelite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vapuss.hikelite.data.model.Trail
import com.vapuss.hikelite.viewmodel.MountainViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MountainMapScreen(
    viewModel: MountainViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val selectedMountain by viewModel.selectedMountain.collectAsState()
    val weatherState   by viewModel.weatherState.collectAsState()
    val isLoading      by viewModel.isLoadingWeather.collectAsState()
    val isDarkMode     by viewModel.isDarkMode.collectAsState()

    // Centered on Romania
    val romaniaCenter = LatLng(45.9432, 24.9668)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(romaniaCenter, 7f)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("HikeLite 🏔") },
                actions = {
                    Text(if (isDarkMode) "🌙" else "☀️", modifier = Modifier.padding(end = 8.dp))
                    Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() })
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- MAP ---
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState
            ) {
                viewModel.trails.forEach { trail ->
                    Marker(
                        state = MarkerState(position = LatLng(trail.latitude, trail.longitude)),
                        title = trail.name,
                        snippet = trail.difficulty,
                        onClick = {
                            viewModel.selectTrail(trail)
                            false
                        }
                    )
                }
            }

            // --- WEATHER CARD (bottom overlay) ---
            selectedMountain?.let { trail ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(trail.name, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            weatherState?.let { w ->
                                Text("🌡 Temperature: ${w.temp}")
                                Text("⛅ Conditions: ${w.condition}")
                                Text("⚠️ Risk: ${w.risk}")
                            } ?: Text("Tap a pin to see weather.")
                        }

                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { viewModel.clearSelection() }) {
                                Text("Close")
                            }
                            Button(onClick = { onNavigateToDetails(trail.name) }) {
                                Text("View Details")
                            }
                        }
                    }
                }
            }
        }
    }
}
