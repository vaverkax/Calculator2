package com.example.calculator

fun main() {
    val calculator = CommandProcessor()
    printHelp()
    while (true) {
        val line = readLine()
        when {
            (line?.toString() == "help") -> printHelp()
            (line?.toString() == "quit") -> return
            else -> {
                when (val result = proceedOrdinaryOperations(calculator, line) ) {
                    is CommandResult.Error -> {
                        println(result.message)
                    }
                    is CommandResult.Success -> {
                        val result = calculator.getResult()
                        if ( result.toDouble() == 0.0) {
                            println(0)
                        } else {
                            println(result)
                        }
                    }
                }
//                val indexOfFirstOpenBracket = line?.indexOfFirst { it.toString() == "(" }
//                val indexOfFirstCloseBracket = line?.indexOfFirst { it.toString() == ")" }
//                if (indexOfFirstOpenBracket != null) {
//                    when {
//                        indexOfFirstCloseBracket == -1 && indexOfFirstOpenBracket == -1 -> {
//                            when (val result = proceedOrdinaryOperations(calculator, line) ) {
//                                is CommandResult.Error -> {
//                                    println(result.message)
//                                    return
//                                }
//                                is CommandResult.Success -> {
//                                    println(calculator.getResult())
//                                }
//                            }
//                        }
//                        (indexOfFirstOpenBracket == -1 && indexOfFirstCloseBracket != -1) ||
//                                (indexOfFirstOpenBracket != -1 && indexOfFirstCloseBracket == -1) -> {
//                            println(CommandResult.WRONG_INPUT)
//                        }
//                        indexOfFirstOpenBracket > indexOfFirstCloseBracket!! -> {
//                            println(CommandResult.WRONG_INPUT)
//                        }
//                        else -> {
//                            if (line?.count { it.toString() == "(" } != line?.count { it.toString() == ")" }) {
//                                println(CommandResult.WRONG_INPUT)
//                            } else {
//                                proceedOperationsWithBrackets(calculator, line)
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}

fun proceedOperationsWithBrackets(calculator: CommandProcessor, line: String?) {
    val listOfNestedStrings = mutableListOf<String>()
    recursiveSplit(calculator,line,listOfNestedStrings, 0)
}

fun recursiveSplit(calculator: CommandProcessor, line: String?, listOfNestedStrings: MutableList<String>, startedIndex: Int) {
    val firsCloseBracketIndex = line!!.indexOf(")", startedIndex)
    val firstOpenBracketIndex =  line!!.indexOf("(", startedIndex)
    val nextOpenBracketIndex = line!!.indexOf("(", firstOpenBracketIndex + 1)
    when (firstOpenBracketIndex) {
        -1 -> {
            if ( firstOpenBracketIndex == -1 && firsCloseBracketIndex == -1) {
                proceedNestedList(calculator,listOfNestedStrings, line)
            } else {
                val lastIndex = line!!
                    .substring(0,firsCloseBracketIndex)
                    .lastIndexOf("(")
                listOfNestedStrings.add(line!!.substring(lastIndex,firsCloseBracketIndex + 1))
                val newLine = line!!.substring(0, lastIndex) +
                        "nested|${listOfNestedStrings.count() - 1}|" +
                        line!!.substring(firsCloseBracketIndex + 1)

                recursiveSplit(calculator,newLine, listOfNestedStrings,0)
            }
        }
        else -> {
            if (firsCloseBracketIndex > nextOpenBracketIndex) {
                if (firstOpenBracketIndex < nextOpenBracketIndex) {
                    recursiveSplit(calculator,line, listOfNestedStrings, nextOpenBracketIndex)
                } else {
                    when (nextOpenBracketIndex) {
                        -1 -> {
                            listOfNestedStrings.add(line!!.substring(firstOpenBracketIndex,firsCloseBracketIndex + 1))
                            val newLine = line!!.substring(0, firstOpenBracketIndex) +
                                    "nested|${listOfNestedStrings.count() - 1}|" +
                                    line!!.substring(firsCloseBracketIndex + 1)

                            recursiveSplit(calculator,newLine, listOfNestedStrings,0)
                        }
                        else -> {
                            print(true)
                        }
                    }
                }
            } else {
                listOfNestedStrings.add(line!!.substring(firstOpenBracketIndex, firsCloseBracketIndex + 1))
                val newLine = line!!.substring(0, firstOpenBracketIndex) +
                        "nested|${listOfNestedStrings.count() - 1}|" +
                        line!!.substring(firsCloseBracketIndex + 1)

                recursiveSplit(calculator,newLine, listOfNestedStrings,firstOpenBracketIndex)
            }
        }
    }
}

fun proceedNestedList(calculator: CommandProcessor,
                      listOfNestedStrings: MutableList<String>,
                      line: String?) {
    for (nest in listOfNestedStrings) {
        if (nest.contains("nested")) {
            var result = proceedOrdinaryOperations(calculator,
                parseNest(nest,listOfNestedStrings)
                    .replace("(","")
                    .replace(")","")
                    .replace("|", ""))
            when (result) {
                is CommandResult.Error -> {
                    println(result.message)
                    return
                }
                is CommandResult.Success -> {
                    val value = calculator.getResult()
                    listOfNestedStrings[listOfNestedStrings.indexOf(nest)] = value.toString()
                }
            }
        } else {
            var result = proceedOrdinaryOperations(calculator,
                nest.replace("(","")
                    .replace(")",""))
            when (result) {
                is CommandResult.Error -> {
                    println(result.message)
                    return
                }
                is CommandResult.Success -> {
                    val value = calculator.getResult()
                    listOfNestedStrings[listOfNestedStrings.indexOf(nest)] = value.toString()
                }
            }
        }
    }
    if (line!!.contains("nested")) {
        var lastLine = line.substring(0,line.indexOf("n")) +
                listOfNestedStrings.last().toString() +
                line.substring(line.lastIndexOf("|") + 1)
        calculator.resetResult()
        if (lastLine.first().toString() == "-") {
            lastLine = "0$lastLine"
        }
        val result = proceedOrdinaryOperations(calculator,lastLine)
        when (result) {
            is CommandResult.Error -> {
                println(result.message)
                return
            }
            is CommandResult.Success -> {
                val value = calculator.getResult()
                listOfNestedStrings.drop(listOfNestedStrings.count())
                println(calculator.getResult())
            }
        }
    }
}

fun parseNest(nest: String, listOfNestedStrings: MutableList<String>): String {
    val indexOfNeededNest = nest
        .substring(nest.indexOf("|") + 1,
            nest
                .lastIndexOf("|"))
        .toInt()
    return nest.substring(0,nest.indexOf("n")) +
            listOfNestedStrings[indexOfNeededNest] +
            nest.substring(nest.lastIndexOf("|") + 1)
}

fun proceedOrdinaryOperations(calculator: CommandProcessor, line: String?): CommandResult {
    val operationsList = Validator().validateOperationList(line)
    val numbersList = Validator().validateNumbersList(line)
    calculator.setIsFirstDigit(Validator().checkFirstSymbol(line))
    return calculator.startProcessing(
        numbersList,
        operationsList ?: emptyList()
    )
}

fun printHelp() {
    println("This is simple calculator. You can use symbols + - * / for plus, minus, times and divide operations.\n" +
            "After you calculate result you can simply continue entering operation e.g.\n" +
            "Input: 2+2\n" +
            "Output: 4.000000000\n" +
            "Input: +4\n" +
            "Output: 8.000000000\n" +
            "For terminating the app simply write quit.\n" +
            "To show this message again type help." +
            "Brackets of any type is forbidden.\n")
}

