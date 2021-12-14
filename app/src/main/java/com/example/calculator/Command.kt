package com.example.calculator

import java.math.BigDecimal
import java.math.RoundingMode

class OrderPlusCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber + secondNumber
    }
}

class OrderMinusCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber - secondNumber
    }
}

class OrderTimesCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal {
        return firstNumber * secondNumber
    }
}

class OrderDivCommand(private val firstNumber: BigDecimal, private val secondNumber: BigDecimal): Command {
    override fun execute(): BigDecimal? {
        return if (secondNumber.compareTo(BigDecimal.ZERO) == 0) {
            null
        } else {
            firstNumber / secondNumber
        }
    }
}

private operator fun BigDecimal.plus(secondNumber: BigDecimal): BigDecimal {
    return this.add(secondNumber).setPrecision(10)
}

private operator fun BigDecimal.minus(secondNumber: BigDecimal): BigDecimal {
    return this.subtract(secondNumber).setPrecision(10)
}

private operator fun BigDecimal.div(secondNumber: BigDecimal): BigDecimal {
    return this.divide(secondNumber, 10,RoundingMode.HALF_UP)
}

private operator fun BigDecimal.times(secondNumber: BigDecimal): BigDecimal {
    return this.multiply(secondNumber).setPrecision(10)
}

private fun BigDecimal.setPrecision(newPrecision: Int) = setScale(scale() + (newPrecision - precision()), RoundingMode.HALF_UP)