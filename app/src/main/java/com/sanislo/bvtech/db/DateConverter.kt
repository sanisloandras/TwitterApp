package com.sanislo.bvtech.db

import androidx.room.TypeConverter
import java.util.*

public class DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
}