package com.example.sneaknest.utils

object ValidateUtils {
    // 正则表达式
    private const val POSTAL_CODE_REGEX = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$"
    private const val CARD_NUMBER_REGEX = "^\\d{13,19}$"

    fun isValidPostalCode(postalCode: String): Boolean {
        return postalCode.matches(POSTAL_CODE_REGEX.toRegex())
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.matches(CARD_NUMBER_REGEX.toRegex())
    }

    fun isNumeric(input: String?): Boolean {
        return input?.all { it.isDigit() } ?: false
    }
}
