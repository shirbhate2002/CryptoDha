package dev.vaidilya.cryptodha.core

import kotlin.math.abs

fun formatCryptoPrice(priceStr: String): String {
    val price = priceStr.toDoubleOrNull() ?: return priceStr
    return when {
        price >= 1_000  -> "${"%,.2f".format(price)} $ "
        price >= 1      -> "${"%,.4f".format(price)} $ "
        price >= 0.01   -> "${"%,.4f".format(price)} $ "
        price >= 0.0001 -> "${"%,.6f".format(price)} $ "
        else            -> "${"%,.8f".format(price)} $ "
    }
}

fun formateToCompactNumber(priceStr: String): String{
    val number = priceStr.toDoubleOrNull() ?: return priceStr

    val absNumber = abs(number)

    val (value, suffix) = when {
        absNumber >= 1_000_000_000_000 -> number / 1_000_000_000_000 to "T"
        absNumber >= 1_000_000_000 -> number / 1_000_000_000 to "B"
        absNumber >= 1_000_000 -> number / 1_000_000 to "M"
        absNumber >= 1_000 -> number / 1_000 to "K"
        else -> return number.toLong().toString()
    }

    return if (value % 1.0 == 0.0) {
        "${value.toLong()}$suffix $"
    } else {
        "%.2f%s $".format(value, suffix)
            .replace(Regex("0+$"), "")
            .replace(Regex("\\.$"), "")
    }
}