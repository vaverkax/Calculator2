package com.example.calculator

import androidx.core.text.isDigitsOnly
import java.math.BigDecimal

object Validator {
    private val regexNumbers = "[^0-9.]".toRegex()
    private val regexOperations = "[^+/*-]".toRegex()

    fun validateOperationList(line: String?): List<String>? {
        return  line?.filter { !it.isWhitespace() }
            ?.replace("*", " * ")
            ?.replace("/", " / ")
            ?.replace("+", " + ")
            ?.replace("-", " - ")
            ?.replace(".","")
            ?.split(" ")
            ?.filter { it.isNotEmpty() }
            ?.filter { !it.isDigitsOnly() }
            ?.filter { !it.matches(regexOperations) }
    }

    fun validateNumbersList(line: String?): List<BigDecimal?>? {
        return line?.filter { !it.isWhitespace() }
            ?.split(regexNumbers)
            ?.map { it.toBigDecimalOrNull() }
    }

    fun isNumbersContainNull(list: List<BigDecimal?>?): Boolean {
        return (list?.any { it == null } == true)
    }

    fun checkFirstSymbol(line: String?): Int? {
        return line?.firstOrNull()?.digitToIntOrNull()
    }

}