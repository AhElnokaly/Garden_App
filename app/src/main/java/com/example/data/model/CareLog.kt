package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "care_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserPlant::class,
            parentColumns = ["id"],
            childColumns = ["user_plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_plant_id")]
)
data class CareLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val user_plant_id: Int,
    val action_type: String, // "watered", "fertilized", "photo", "note"
    val timestamp: Long = System.currentTimeMillis(),
    val note_text: String? = null,
    val photo_uri: String? = null
)
