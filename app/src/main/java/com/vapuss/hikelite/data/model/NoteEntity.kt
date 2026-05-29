package com.vapuss.hikelite.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mountainName: String,
    val textContent: String,
    val timestamp: Long = System.currentTimeMillis()
)
