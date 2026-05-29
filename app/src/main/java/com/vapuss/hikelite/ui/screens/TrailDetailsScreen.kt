package com.vapuss.hikelite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vapuss.hikelite.data.model.NoteEntity
import com.vapuss.hikelite.viewmodel.MountainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailsScreen(
    viewModel: MountainViewModel,
    mountainName: String,
    onBack: () -> Unit
) {
    val notes        by viewModel.notes.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()

    var noteText by remember { mutableStateOf("") }

    // Load notes when the screen opens
    LaunchedEffect(mountainName) {
        viewModel.loadNotes(mountainName)
    }

    // Find the trail from the static list
    val trail = viewModel.trails.find { it.name == mountainName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mountainName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Trail details ---
            item {
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trail Details", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Difficulty: ${trail?.difficulty ?: "N/A"}")
                        Text(trail?.description ?: "")
                    }
                }
            }

            // --- Current weather ---
            item {
                weatherState?.let { w ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Current Weather", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("🌡 ${w.temp}  ⛅ ${w.condition}  ⚠️ Risk: ${w.risk}")
                        }
                    }
                }
            }

            // --- Add note ---
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add Note", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("e.g. Trail blocked by snow...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.saveNote(mountainName, noteText)
                                noteText = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = noteText.isNotBlank()
                        ) {
                            Text("Save Note")
                        }
                    }
                }
            }

            // --- Saved notes list ---
            if (notes.isNotEmpty()) {
                item {
                    Text("Saved Notes (${notes.size})", style = MaterialTheme.typography.titleMedium)
                }
                items(notes, key = { it.id }) { note ->
                    NoteCard(note = note, onDelete = { viewModel.deleteNote(note) })
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun NoteCard(note: NoteEntity, onDelete: () -> Unit) {
    val dateFormatted = remember(note.timestamp) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            .format(Date(note.timestamp))
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.textContent, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text(dateFormatted, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
