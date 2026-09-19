package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_plants",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plant_id")]
)
data class UserPlant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plant_id: Int,
    val nickname: String,
    val place: String = "البلكونة",
    val added_date: Long = System.currentTimeMillis()
)
