package com.vapuss.hikelite.data.repository

import com.vapuss.hikelite.data.local.NoteDao
import com.vapuss.hikelite.data.model.NoteEntity
import com.vapuss.hikelite.data.model.Trail
import com.vapuss.hikelite.data.model.WeatherResponse
import com.vapuss.hikelite.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class MountainRepository(private val noteDao: NoteDao) {

    val trails: List<Trail> = listOf(
        Trail(
            name = "Bucegi",
            latitude = 45.4101,
            longitude = 25.4537,
            difficulty = "Medium",
            description = "Massif with a vast plateau, accessible by cable car from Sinaia or Busteni."
        ),
        Trail(
            name = "Fagaras",
            latitude = 45.6030,
            longitude = 24.7362,
            difficulty = "Hard",
            description = "The highest massif in Romania, featuring Moldoveanu Peak (2544m)."
        ),
        Trail(
            name = "Piatra Craiului",
            latitude = 45.5100,
            longitude = 25.2200,
            difficulty = "Hard",
            description = "Spectacular limestone ridge, one of the most beautiful in the country."
        )
    )

    suspend fun getWeather(mountain: String): WeatherResponse =
        RetrofitClient.weatherApiService.getWeather(mountain)

    fun getNotesForMountain(mountainName: String): Flow<List<NoteEntity>> =
        noteDao.getNotesForMountain(mountainName)

    suspend fun saveNote(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
