package com.github.manosbatsis.lib.test

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

object MyLocalDateHandler {
    private const val PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    private const val ZONE_ID = "UTC"
    private val DTF = DateTimeFormatter.ofPattern(PATTERN)
    fun fromStringToDate(string: String?): Date {
        val ldt = LocalDateTime.parse(string, DTF)
        val zdt = ldt.atZone(ZoneId.of(ZONE_ID))
        return Date.from(zdt.toInstant())
    }

    fun fromDateToString(date: Date): String {
        val zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of(ZONE_ID))
        return zdt.format(DTF)
    }
}
