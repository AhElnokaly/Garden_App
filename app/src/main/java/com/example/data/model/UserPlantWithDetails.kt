package com.example.data.model

data class UserPlantWithDetails(
    val userPlant: UserPlant,
    val plant: Plant,
    val lastWateredLog: CareLog?,
    val latestCareLog: CareLog?
) {
    /**
     * Calculates whether the plant needs watering now based on water_frequency_days
     * and the timestamp of the last watering log (or added_date if never watered yet).
     */
    val needsWatering: Boolean
        get() {
            val lastWaterTime = lastWateredLog?.timestamp ?: userPlant.added_date
            val elapsedDays = (System.currentTimeMillis() - lastWaterTime) / (1000L * 60 * 60 * 24)
            return elapsedDays >= plant.water_frequency_days
        }

    val daysSinceWatered: Int
        get() {
            val lastWaterTime = lastWateredLog?.timestamp ?: return -1
            return ((System.currentTimeMillis() - lastWaterTime) / (1000L * 60 * 60 * 24)).toInt()
        }
}
