package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "containers",
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("place_id")
    ]
)
data class PlantContainer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val place_id: Int,
    val name: String,
    val type: ContainerType = ContainerType.POT,
    val material: String? = null,
    val width: Float? = null,
    val length: Float? = null,
    val height: Float? = null,
    val volume: Float? = null,
    val drainage: Boolean? = true,
    val growing_method: GrowingMethod = GrowingMethod.SOIL,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)
