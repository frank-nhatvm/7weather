package com.fatherofapps.androidbase.common

import org.threeten.bp.OffsetDateTime

fun OffsetDateTime.isToday(): Boolean{
    val today = OffsetDateTime.now()
    return (this.dayOfMonth == today.dayOfMonth && this.month == today.month && this.year == today.year)
}