package com.example.calculator

import java.math.BigDecimal

interface Command {
    fun execute(): BigDecimal?
}

sealed class CommandResult {
    object Success : CommandResult()
    class Error(val message: String) : CommandResult()

    companion object {
        const val WRONG_INPUT = "Wrong input! Please try again."
        const val DIV_BY_ZERO = "Dividing by zero is forbidden! Please try again."
    }
}

class CommandProcessor {
    private val queue = mutableListOf<Command>()
    private var result: BigDecimal? = BigDecimal.ZERO
    private var previousResult: BigDecimal = BigDecimal.ZERO
    private var isCheckingFirstSymbol = true
    private val numbers = mutableListOf<BigDecimal>()
    private var operations = mutableListOf<String>()
    private var isFirstDigit: Int? = null

    private fun doRoutine(index: Int) {
        processLastCommand()
        numbers[index] = result ?: BigDecimal(0)
        numbers.removeAt(index + 1)
        operations.removeAt(index)
    }

    private fun setIsFirstDigit(value: Int?) {
        isFirstDigit = value
    }

    private fun resetResult() {
        result = BigDecimal.ZERO
    }

    private fun startProcessing(
        numberList: List<BigDecimal?>?,
        operationsList: List<String>
    ): CommandResult {
        when (Validator.isNumbersContainNull(numberList)) {
            true -> {
                val mutableListNumber = numberList?.toMutableList()
                val operationMutableList = operationsList?.toMutableList()
                val indexOfNull = mutableListNumber?.indexOf(null) ?: 0
                if (operationsList[indexOfNull] == "-") {
                    when {
                        (indexOfNull + 1) > ((mutableListNumber?.count() ?: 0) + 1) -> {
                            return CommandResult.Error(CommandResult.WRONG_INPUT)
                        }
                        else -> {
                            isFirstDigit = mutableListNumber?.get(indexOfNull + 1)?.toInt()
                            mutableListNumber?.set(
                                indexOfNull + 1,
                                mutableListNumber[indexOfNull + 1]?.negate()
                            )
                            mutableListNumber?.removeAt(indexOfNull)
                            operationMutableList[indexOfNull] = ""
                            return when (startProcessing(mutableListNumber, operationMutableList)) {
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
                return when (isCheckingFirstSymbol) {
                    true -> {
                        isCheckingFirstSymbol = false
                        CommandResult.Error(CommandResult.WRONG_INPUT)
                    }
                    else -> {
                        isCheckingFirstSymbol = true
                        startProcessing(numberList?.drop(1), operationsList)
                    }
                }
            }
            else -> {
                if (isFirstDigit != null) {
                    result = BigDecimal(0)
                    numbers.clear()
                }
                addToQueue(numberList?.filterNotNull() ?: emptyList(), operationsList)
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

    private fun addToQueue(numberList: List<BigDecimal>, operationsList: List<String>) {
        numbers.addAll(numberList.toMutableList())
        operations.addAll(operationsList.toMutableList())
        if (numbers.count() == operations.count()) {
            operations = operations.filter { it != "-" }.toMutableList()
        }
        val firstEmpty = operations.indexOfFirst { it == "" }
        if (firstEmpty == 0) {
            operations = operations.drop(1).toMutableList()
        }
        while (true) {
            val indexOfPrimaryOperation = operations.indexOfFirst { it == "*" || it == "/" }
            if (indexOfPrimaryOperation == -1) {
                break
            }
            when (operations[indexOfPrimaryOperation]) {
                "*" -> {
                    queue.add(
                        OrderTimesCommand(
                            numbers[indexOfPrimaryOperation],
                            numbers[indexOfPrimaryOperation + 1]
                        )
                    )
                    doRoutine(indexOfPrimaryOperation)
                }
                "/" -> {
                    queue.add(
                        OrderDivCommand(
                            numbers[indexOfPrimaryOperation],
                            numbers[indexOfPrimaryOperation + 1]
                        )
                    )
                    doRoutine(indexOfPrimaryOperation)
                }
                else -> break
            }
        }
        while (true) {
            val indexOfOperation = operations.indexOfFirst { it == "+" || it == "-" }
            if (indexOfOperation == -1) {
                break
            }
            when (operations[indexOfOperation]) {
                "-" -> {
                    if (numbers[indexOfOperation + 1] < BigDecimal(0)) {
                        queue.add(
                            OrderMinusCommand(
                                numbers[indexOfOperation],
                                numbers[indexOfOperation + 1].abs()
                            )
                        )
                    } else {
                        queue.add(
                            OrderMinusCommand(
                                numbers[indexOfOperation],
                                numbers[indexOfOperation + 1]
                            )
                        )
                    }
                    doRoutine(indexOfOperation)
                }
                "+" -> {
                    queue.add(
                        OrderPlusCommand(
                            numbers[indexOfOperation],
                            numbers[indexOfOperation + 1]
                        )
                    )
                    doRoutine(indexOfOperation)
                }
                else -> break
            }
        }
        isCheckingFirstSymbol = false
        if (operations.contains("")) {
            if (numbers.count() > 1) {
                while (numbers.count() != 1) {
                    queue.add(OrderPlusCommand(numbers[0], numbers[1]))
                    doRoutine(0)
                }
            } else {
                operations = mutableListOf()
            }
        }
    }

    private fun processLastCommand() {
        previousResult = result ?: BigDecimal(0)
        result = queue.last().execute()
        queue.removeAt(0)
    }

    fun getResult(): BigDecimal {
        return result ?: BigDecimal.ZERO
    }

    fun proceedOperationsWithBrackets(
        calculator: CommandProcessor,
        line: String
    ) {
        val listOfNestedStrings = mutableListOf<String>()
//        recursiveSplit(calculator,validator, line,listOfNestedStrings, 0)
    }

//    fun recursiveSplit(calculator: CommandProcessor, // переделать на рекурсивный вызов с возвращением List<String и не использовать в параметраз MutableList
//                       validator: Validator,
//                       line: String,
////                       listOfNestedStrings: MutableList<String>,
//                       startedIndex: Int) : List<String> {
//        val firsCloseBracketIndex = line.indexOf(")", startedIndex) ?: -1
//        val firstOpenBracketIndex =  line.indexOf("(", startedIndex)  ?: -1
//        val nextOpenBracketIndex = line.indexOf("(", firstOpenBracketIndex + 1) ?: -1
//        when (firstOpenBracketIndex) {
//            -1 -> {
//                if ( firstOpenBracketIndex == -1 && firsCloseBracketIndex == -1) {
//                    proceedNestedList(calculator, validator, listOfNestedStrings, line)
//                } else {
//                    val lastIndex = line
//                        .substring(0,firsCloseBracketIndex)
//                        .lastIndexOf("(")
//                    listOfNestedStrings.add(line.substring(lastIndex, firsCloseBracketIndex + 1) )
//                    val newLine = line.substring(0, lastIndex) +
//                            "nested|${listOfNestedStrings.count() - 1}|" +
//                            line.substring(firsCloseBracketIndex + 1)
//
//                    recursiveSplit(calculator, validator, newLine, listOfNestedStrings,0)
//                }
//            }
//            else -> {
//                if (firsCloseBracketIndex > nextOpenBracketIndex) {
//                    if (firstOpenBracketIndex < nextOpenBracketIndex) {
//                        recursiveSplit(calculator, validator, line, listOfNestedStrings, nextOpenBracketIndex)
//                    } else {
//                        when (nextOpenBracketIndex) {
//                            -1 -> {
//                                listOfNestedStrings.add(line.substring(firstOpenBracketIndex,firsCloseBracketIndex + 1))
//                                val newLine = line.substring(0, firstOpenBracketIndex) +
//                                        "nested|${listOfNestedStrings.count() - 1}|" +
//                                        line.substring(firsCloseBracketIndex + 1)
//
//                                recursiveSplit(calculator, validator, newLine, listOfNestedStrings,0)
//                            }
//                            else -> {
//                                print(true)
//                            }
//                        }
//                    }
//                } else {
//                    listOfNestedStrings.add(line.substring(firstOpenBracketIndex, firsCloseBracketIndex + 1))
//                    val newLine = line.substring(0, firstOpenBracketIndex) +
//                            "nested|${listOfNestedStrings.count() - 1}|" +
//                            line.substring(firsCloseBracketIndex + 1)
//
//                    recursiveSplit(calculator,validator, newLine, listOfNestedStrings,firstOpenBracketIndex)
//                }
//            }
//        }
//    }

    fun proceedNestedList(
        calculator: CommandProcessor,
        listOfNestedStrings: List<String>,
        line: String
    ) {
        val mutableListOfStrings = listOfNestedStrings.toMutableList()
        for (nest in mutableListOfStrings) {
            if (nest.contains("nested")) {
                var result = proceedOrdinaryOperations(
                    calculator,
                    parseNest(nest, listOfNestedStrings)
                        .replace("(", "")
                        .replace(")", "")
                        .replace("|", "")
                )
                when (result) {
                    is CommandResult.Error -> {
                        println(result.message)
                        return
                    }
                    is CommandResult.Success -> {
                        val value = calculator.getResult()
                        mutableListOfStrings[mutableListOfStrings.indexOf(nest)] = value.toString()
                    }
                }
            } else {
                var result = proceedOrdinaryOperations(
                    calculator,
                    nest.replace("(", "")
                        .replace(")", "")
                )
                when (result) {
                    is CommandResult.Error -> {
                        println(result.message)
                        return
                    }
                    is CommandResult.Success -> {
                        val value = calculator.getResult()
                        mutableListOfStrings[mutableListOfStrings.indexOf(nest)] = value.toString()
                    }
                }
            }
        }
        if (line.contains("nested")) {
            var lastLine = line.substring(0, line.indexOf("n")) +
                    listOfNestedStrings.last().toString() +
                    line.substring(line.lastIndexOf("|") + 1)
            calculator.resetResult()
            if (lastLine.first().toString() == "-") {
                lastLine = "0$lastLine"
            }
            when (val result = proceedOrdinaryOperations(calculator, lastLine)) {
                is CommandResult.Error -> {
                    println(result.message)
                    return
                }
                is CommandResult.Success -> {
                    listOfNestedStrings.drop(listOfNestedStrings.count())
                    println(calculator.getResult())
                }
            }
        }
    }

    fun parseNest(
        nest: String,
        listOfNestedStrings: List<String>
    ): String {
        val indexOfNeededNest = nest
            .substring(
                nest.indexOf("|") + 1,
                nest
                    .lastIndexOf("|")
            )
            .toInt()
        return nest.substring(0, nest.indexOf("n")) +
                listOfNestedStrings[indexOfNeededNest] +
                nest.substring(nest.lastIndexOf("|") + 1)
    }

    private fun proceedOrdinaryOperations(
        calculator: CommandProcessor,
        line: String
    ): CommandResult {
        return with(Validator) {
            calculator.setIsFirstDigit(checkFirstSymbol(line))
            calculator.startProcessing(
                validateNumbersList(line),
                validateOperationList(line) ?: emptyList()
            )
        }
    }

    fun startOperations(calculator: CommandProcessor, line: String): Pair<BigDecimal?, String> {
        return when (val result = calculator.proceedOrdinaryOperations(calculator, line)) {
            is CommandResult.Error -> {
                Pair(null, result.message)
            }
            is CommandResult.Success -> {
                Pair(calculator.getResult(), "")
            }
        }
    }
}