package com.example.calculator

import java.math.BigDecimal

interface Command {
    fun execute(): BigDecimal?
}

sealed class CommandResult {
    object Success : CommandResult()
    class Error(val message: String): CommandResult()

    companion object {
        const val WRONG_INPUT = "Wrong input! Please try again."
        const val DIV_BY_ZERO = "Dividing by zero is forbidden! Please try again."
    }
}

class CommandProcessor {
    private val queue = mutableListOf<Command>()
    private var result: BigDecimal? = BigDecimal(0)
    private var previousResult: BigDecimal = BigDecimal(0)
    private var isCheckingFirstSymbol = true
    private val numbers = mutableListOf<BigDecimal>()
    private var operations = mutableListOf<String>()
    private var isFirstDigit: Int? = null
    private var isNestedOperations = false

    private fun doRoutine(index: Int) {
        processLastCommand()
        numbers[index] = result ?: BigDecimal(0)
        numbers.removeAt(index + 1)
        operations.removeAt(index)
    }

    fun setIsNestedOperations(check: Boolean) {
        isNestedOperations = check
    }

    fun setIsFirstDigit(value: Int?) {
        isFirstDigit = value
    }

    fun resetResult() {
        result = BigDecimal(0)
    }

    fun startProcessing(numberList: List<BigDecimal?>?, operationsList: List<String>): CommandResult {
        when (Validator().isNumbersContainNull(numberList)) {
            true ->  {
                return when (isCheckingFirstSymbol) {
                    true -> {
                        isCheckingFirstSymbol = false
                        CommandResult.Error(CommandResult.WRONG_INPUT)
                    }
                    else -> {
                        isCheckingFirstSymbol = true
                        startProcessing(numberList?.drop(1),operationsList)
                    }
                }
            }
            else -> {
                if (isFirstDigit != null ) {
                    result = BigDecimal(0)
                    numbers.clear()
                }
                addToQueue(numberList?.map { it ?: 0 } as List<BigDecimal>? ?: emptyList(), operationsList)
                return when (result) {
                    null -> {
                        result = previousResult
                        numbers[0] = previousResult
                        CommandResult.Error(CommandResult.DIV_BY_ZERO)
                    }
                    else -> CommandResult.Success
                }
            }
        }
    }

    private fun addToQueue(numberList: List<BigDecimal>, operationsList: List<String>): CommandProcessor =
        apply {
            numbers.addAll(numberList.toMutableList())
            operations.addAll(operationsList.toMutableList())
            if (numbers.count() == operations.count()) {
                operations = operations.filter { it != "-" }.toMutableList()
            }
            while (true) {
                val indexOfPrimaryOperation = operations.indexOfFirst { it == "*" || it == "/" }
                if (indexOfPrimaryOperation == -1) {
                    break
                }
                when (operations[indexOfPrimaryOperation]) {
                    "*" -> {
                        queue.add(OrderTimesCommand(numbers[indexOfPrimaryOperation], numbers[indexOfPrimaryOperation + 1]))
                        doRoutine(indexOfPrimaryOperation)
                    }
                    "/" -> {
                        queue.add(OrderDivCommand(numbers[indexOfPrimaryOperation], numbers[indexOfPrimaryOperation + 1]))
                        doRoutine(indexOfPrimaryOperation)
                    }
                    else -> break
                }
            }
            while (true) {
                val indexOfOperation = operations.indexOfFirst { it == "+" || it == "-"  }
                if (indexOfOperation == -1) {
                    break
                }
                when (operations[indexOfOperation]) {
                    "-" -> {
                        if (numbers[indexOfOperation + 1] < BigDecimal(0)) {
                            queue.add(OrderMinusCommand(numbers[indexOfOperation], numbers[indexOfOperation + 1].abs()))
                        } else {
                            queue.add(OrderMinusCommand(numbers[indexOfOperation], numbers[indexOfOperation + 1]))
                        }
                        doRoutine(indexOfOperation)
                    }
                    "+" -> {
                        queue.add(OrderPlusCommand(numbers[indexOfOperation], numbers[indexOfOperation + 1]))
                        doRoutine(indexOfOperation)
                    }
                    else -> break
                }
            }
            isCheckingFirstSymbol = false
        }

    private fun processLastCommand(): CommandProcessor =
        apply {
            previousResult = result ?: BigDecimal(0)
            result = if (queue.last().execute() == null) {
                null
            } else {
                queue.last().execute() as BigDecimal
            }
            queue.removeAt(0)
        }

    fun getResult(): BigDecimal {
        return result ?: BigDecimal(0)
    }
}