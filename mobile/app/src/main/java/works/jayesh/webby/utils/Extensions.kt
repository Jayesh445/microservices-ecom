package works.jayesh.webby.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun Double.toRupees(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    return format.format(this)
}

fun String.toFormattedDate(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = parser.parse(this)
        date?.let { formatter.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toFormattedDateTime(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = parser.parse(this)
        date?.let { formatter.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return emailRegex.matches(this)
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^[6-9]\\d{9}$".toRegex()
    return phoneRegex.matches(this)
}

fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

fun Double.calculateDiscount(originalPrice: Double): Int {
    if (originalPrice <= 0) return 0
    return (((originalPrice - this) / originalPrice) * 100).toInt()
}
