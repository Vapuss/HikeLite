package com.vapuss.hikelite.data.local

import androidx.room.*
import com.vapuss.hikelite.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE mountainName = :mountainName ORDER BY timestamp DESC")
    fun getNotesForMountain(mountainName: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE mountainName = :mountainName")
    suspend fun deleteAllForMountain(mountainName: String)
}
