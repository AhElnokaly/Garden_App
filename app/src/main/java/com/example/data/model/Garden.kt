package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gardens")
data class Garden(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)
