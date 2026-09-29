package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "soil_components",
    foreignKeys = [
        ForeignKey(
            entity = SoilProfile::class,
            parentColumns = ["id"],
            childColumns = ["soil_profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("soil_profile_id")
    ]
)
data class SoilComponent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val soil_profile_id: Int,
    val component_name: String,
    val percentage: Float? = null,
    val notes: String? = null
)
