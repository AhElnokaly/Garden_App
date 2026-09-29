package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "places",
    foreignKeys = [
        ForeignKey(
            entity = Garden::class,
            parentColumns = ["id"],
            childColumns = ["garden_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("garden_id")
    ]
)
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val garden_id: Int = 1,
    val name: String,
    val created_date: Long = System.currentTimeMillis()
)
