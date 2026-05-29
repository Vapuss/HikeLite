package com.vapuss.hikelite.data.model

data class Trail(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val difficulty: String = "Mediu",
    val description: String = ""
)
