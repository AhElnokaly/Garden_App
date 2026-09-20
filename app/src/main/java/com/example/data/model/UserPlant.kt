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
        ),
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("plant_id"),
        Index("place_id")
    ]
)
data class UserPlant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plant_id: Int,
    val nickname: String,
    val place_id: Int = 1,
    val added_date: Long = System.currentTimeMillis()
)
