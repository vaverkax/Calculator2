package com.example.calculator

fun main() {



//                val indexOfFirstOpenBracket = line?.indexOfFirst { it.toString() == "(" }
//                val indexOfFirstCloseBracket = line?.indexOfFirst { it.toString() == ")" }
//                if (indexOfFirstOpenBracket != null) {
//                    when {
//                        indexOfFirstCloseBracket == -1 && indexOfFirstOpenBracket == -1 -> {
//                            when (val result = calculator.proceedOrdinaryOperations(calculator, line) ) {
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
//                                calculator.proceedOperationsWithBrackets(calculator, line)
//                            }
//                        }
//                    }
//                }

}

