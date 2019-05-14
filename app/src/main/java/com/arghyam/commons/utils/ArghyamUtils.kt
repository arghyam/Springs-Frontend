package com.arghyam.commons.utils

class ArghyamUtils {
    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }
}