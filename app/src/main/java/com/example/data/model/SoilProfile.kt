package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "soil_profiles",
    foreignKeys = [
        ForeignKey(
            entity = PlantContainer::class,
            parentColumns = ["id"],
            childColumns = ["container_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("container_id")
    ]
)
data class SoilProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val container_id: Int,
    val type: String? = null,
    val moisture: Float? = null,
    val pH: Float? = null,
    val ec: Float? = null,
    val soil_temperature: Float? = null,
    val data_source: DataSource = DataSource.USER_ENTERED,
    val last_updated: Long = System.currentTimeMillis()
)
