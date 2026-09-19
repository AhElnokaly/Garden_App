package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name_ar: String,
    val name_en: String,
    val category: String, // "ornamental", "vegetable", "herb"
    val water_frequency_days: Int,
    val light_needs: String,
    val icon_ref: String
)
