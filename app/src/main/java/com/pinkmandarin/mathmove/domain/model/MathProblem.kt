package com.pinkmandarin.mathmove.domain.model

data class MathProblem(
    val operand1: Int,
    val operand2: Int,
    val operator: Operator,
    val answer: Int,
    val choices: List<Int> // 4 choices including the answer
) {
    val questionText: String
        get() = "$operand1 ${operator.symbol} $operand2 = ?"
}

enum class Operator(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×")
}
