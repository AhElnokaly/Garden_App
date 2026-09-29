package com.example.data

import androidx.room.TypeConverter
import com.example.data.model.ContainerType
import com.example.data.model.DataSource
import com.example.data.model.GrowingMethod

class GardenConverters {

    @TypeConverter
    fun fromContainerType(value: ContainerType?): String? = value?.name

    @TypeConverter
    fun toContainerType(value: String?): ContainerType? {
        if (value == null) return null
        return try {
            ContainerType.valueOf(value)
        } catch (_: Exception) {
            ContainerType.OTHER
        }
    }

    @TypeConverter
    fun fromGrowingMethod(value: GrowingMethod?): String? = value?.name

    @TypeConverter
    fun toGrowingMethod(value: String?): GrowingMethod? {
        if (value == null) return null
        return try {
            GrowingMethod.valueOf(value)
        } catch (_: Exception) {
            GrowingMethod.OTHER
        }
    }

    @TypeConverter
    fun fromDataSource(value: DataSource?): String? = value?.name

    @TypeConverter
    fun toDataSource(value: String?): DataSource? {
        if (value == null) return null
        return try {
            DataSource.valueOf(value)
        } catch (_: Exception) {
            DataSource.USER_ENTERED
        }
    }
}
