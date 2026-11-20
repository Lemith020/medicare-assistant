package com.example.medicare_assistant.core

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun formatTime(time: LocalTime, locale: Locale): String =
    time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))

fun formatDate(date: LocalDate, locale: Locale): String =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))