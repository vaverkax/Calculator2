package com.example.calculator

import java.math.BigDecimal
import java.math.RoundingMode

class OrderPlusCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber.add(secondNumber).setPrecision(10)
    }
}

class OrderMinusCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber.subtract(secondNumber).setPrecision(10)
    }
}

class OrderTimesCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber.multiply(secondNumber).setPrecision(10)
    }
}

class OrderDivCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal? {
        return if (secondNumber == BigDecimal.ZERO) {
            null
        } else {
            firstNumber.divide(secondNumber, 10, RoundingMode.HALF_UP)
        }
    }
}

private fun BigDecimal.setPrecision(newPrecision: Int) = setScale(scale() + (newPrecision - precision()), RoundingMode.HALF_UP)