package ru.fefu.store.util

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(priceInKopecks: Long): String {
    val rubles = priceInKopecks / 100
    val formatter = NumberFormat.getIntegerInstance(Locale("ru", "RU"))

    return "${formatter.format(rubles)} ₽"
}