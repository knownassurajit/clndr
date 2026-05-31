package com.clndr.feature.lifegrid.state

import com.clndr.core.datetime.Granularity
import java.time.LocalDate

data class LifeGridState(
    val birthDate: LocalDate? = null,
    val granularity: Granularity = Granularity.WEEKS,
    val packedStates: IntArray = IntArray(0),
    val currentIndex: Int = -1,
    val totalCells: Int = 0,
    val isLoading: Boolean = true,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LifeGridState
        if (birthDate != other.birthDate) return false
        if (granularity != other.granularity) return false
        if (!packedStates.contentEquals(other.packedStates)) return false
        if (currentIndex != other.currentIndex) return false
        if (totalCells != other.totalCells) return false
        if (isLoading != other.isLoading) return false
        return true
    }

    override fun hashCode(): Int {
        var result = birthDate?.hashCode() ?: 0
        result = 31 * result + granularity.hashCode()
        result = 31 * result + packedStates.contentHashCode()
        result = 31 * result + currentIndex
        result = 31 * result + totalCells
        result = 31 * result + isLoading.hashCode()
        return result
    }
}

sealed interface LifeGridIntent {
    data class SetGranularity(val granularity: Granularity) : LifeGridIntent
    data class SetBirthDate(val date: LocalDate) : LifeGridIntent
    data object Refresh : LifeGridIntent
}

sealed interface LifeGridEffect {
    data object ScrollToCurrent : LifeGridEffect
}
