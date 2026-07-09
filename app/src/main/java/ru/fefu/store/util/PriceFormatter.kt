package ru.fefu.store.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatPrice(priceInKopecks: Long): String {
    val locale = Locale.forLanguageTag("ru-RU")
    val rubles = BigDecimal.valueOf(priceInKopecks, 2)
    val hasKopecks = priceInKopecks % KOPECKS_IN_RUBLE != 0L

    val formatter = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = if (hasKopecks) 2 else 0
        maximumFractionDigits = if (hasKopecks) 2 else 0
    }

    return "${formatter.format(rubles)} ₽"
}

private const val KOPECKS_IN_RUBLE = 100L