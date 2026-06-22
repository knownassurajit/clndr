package com.knownassurajit.clndr_widget.core.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.epochSecond

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochSecond)

    @TypeConverter
    fun fromZoneId(value: ZoneId?): String? = value?.id

    @TypeConverter
    fun toZoneId(value: String?): ZoneId? = value?.let(ZoneId::of)
}
