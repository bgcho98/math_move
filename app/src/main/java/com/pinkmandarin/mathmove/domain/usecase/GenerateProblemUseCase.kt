package com.pinkmandarin.mathmove.domain.usecase

import com.pinkmandarin.mathmove.domain.model.MathProblem
import com.pinkmandarin.mathmove.domain.model.Operator
import javax.inject.Inject
import kotlin.random.Random

class GenerateProblemUseCase @Inject constructor() {

    operator fun invoke(stageNumber: Int): MathProblem {
        val (operand1, operand2, operator) = generateOperands(stageNumber)
        val answer = calculateAnswer(operand1, operand2, operator)
        val choices = generateChoices(answer)
        return MathProblem(
            operand1 = operand1,
            operand2 = operand2,
            operator = operator,
            answer = answer,
            choices = choices
        )
    }

    private data class Operands(val op1: Int, val op2: Int, val operator: Operator)

    private fun generateOperands(stage: Int): Operands {
        return when (stage) {
            in 1..5 -> generateAdditionEasy()
            in 6..10 -> generateSubtractionEasy()
            in 11..15 -> generateAdditionMedium()
            in 16..20 -> generateTwoDigitPlusMinus()
            in 21..30 -> generateTwoDigitBoth()
            in 31..40 -> generateMultiplication()
            in 41..50 -> generateMixed(stage)
            else -> generateHard(stage)
        }
    }

    /**
     * Stages 1-5: single digit + single digit, sum <= 10
     */
    private fun generateAdditionEasy(): Operands {
        val op1 = Random.nextInt(1, 10) // 1..9
        val op2 = Random.nextInt(1, 11 - op1) // ensure sum <= 10
        return Operands(op1, op2, Operator.ADD)
    }

    /**
     * Stages 6-10: single digit - single digit, result >= 0
     */
    private fun generateSubtractionEasy(): Operands {
        val op1 = Random.nextInt(1, 10) // 1..9
        val op2 = Random.nextInt(0, op1 + 1) // 0..op1 so result >= 0
        return Operands(op1, op2, Operator.SUBTRACT)
    }

    /**
     * Stages 11-15: single digit + single digit, sum <= 18
     */
    private fun generateAdditionMedium(): Operands {
        val op1 = Random.nextInt(1, 10) // 1..9
        val op2 = Random.nextInt(1, 10) // 1..9, sum can be up to 18
        return Operands(op1, op2, Operator.ADD)
    }

    /**
     * Stages 16-20: two digit +/- single digit
     */
    private fun generateTwoDigitPlusMinus(): Operands {
        val op1 = Random.nextInt(10, 100) // 10..99
        val op2 = Random.nextInt(1, 10) // 1..9
        val operator = if (Random.nextBoolean()) Operator.ADD else Operator.SUBTRACT
        // For subtraction, ensure result >= 0 (always true since op1 >= 10 and op2 <= 9)
        return Operands(op1, op2, operator)
    }

    /**
     * Stages 21-30: two digit +/- two digit
     */
    private fun generateTwoDigitBoth(): Operands {
        val operator = if (Random.nextBoolean()) Operator.ADD else Operator.SUBTRACT
        return if (operator == Operator.SUBTRACT) {
            val op1 = Random.nextInt(10, 100)
            val op2 = Random.nextInt(10, op1 + 1) // ensure result >= 0
            Operands(op1, op2, operator)
        } else {
            val op1 = Random.nextInt(10, 100)
            val op2 = Random.nextInt(10, 100)
            Operands(op1, op2, operator)
        }
    }

    /**
     * Stages 31-40: single digit * single digit
     */
    private fun generateMultiplication(): Operands {
        val op1 = Random.nextInt(2, 10) // 2..9
        val op2 = Random.nextInt(2, 10) // 2..9
        return Operands(op1, op2, Operator.MULTIPLY)
    }

    /**
     * Stages 41-50: mixed operations (add, subtract, multiply)
     */
    private fun generateMixed(stage: Int): Operands {
        return when (Random.nextInt(3)) {
            0 -> {
                // Addition: two digit + two digit
                val op1 = Random.nextInt(10, 100)
                val op2 = Random.nextInt(10, 100)
                Operands(op1, op2, Operator.ADD)
            }
            1 -> {
                // Subtraction: two digit - two digit, result >= 0
                val op1 = Random.nextInt(10, 100)
                val op2 = Random.nextInt(10, op1 + 1)
                Operands(op1, op2, Operator.SUBTRACT)
            }
            else -> {
                // Multiplication: single digit * single digit
                val op1 = Random.nextInt(2, 10)
                val op2 = Random.nextInt(2, 10)
                Operands(op1, op2, Operator.MULTIPLY)
            }
        }
    }

    /**
     * Stages 51+: progressively harder (expand ranges based on stage)
     */
    private fun generateHard(stage: Int): Operands {
        val maxRange = minOf(100 + (stage - 50) * 10, 999) // progressively increase max
        return when (Random.nextInt(3)) {
            0 -> {
                // Addition with larger numbers
                val op1 = Random.nextInt(10, maxRange + 1)
                val op2 = Random.nextInt(10, maxRange + 1)
                Operands(op1, op2, Operator.ADD)
            }
            1 -> {
                // Subtraction with larger numbers
                val op1 = Random.nextInt(10, maxRange + 1)
                val op2 = Random.nextInt(10, op1 + 1)
                Operands(op1, op2, Operator.SUBTRACT)
            }
            else -> {
                // Multiplication: expand to two digit * single digit
                val op1 = Random.nextInt(2, minOf(maxRange / 10 + 2, 100))
                val op2 = Random.nextInt(2, 10)
                Operands(op1, op2, Operator.MULTIPLY)
            }
        }
    }

    private fun calculateAnswer(op1: Int, op2: Int, operator: Operator): Int {
        return when (operator) {
            Operator.ADD -> op1 + op2
            Operator.SUBTRACT -> op1 - op2
            Operator.MULTIPLY -> op1 * op2
        }
    }

    /**
     * Generate 4 unique choices including the correct answer.
     * Wrong choices are within +-1 to +-5 of the answer, no duplicates, no negatives.
     */
    private fun generateChoices(correctAnswer: Int): List<Int> {
        val choices = mutableSetOf(correctAnswer)
        val range = if (correctAnswer <= 10) 3 else 5

        var attempts = 0
        while (choices.size < 4 && attempts < 100) {
            val offset = Random.nextInt(1, range + 1) * if (Random.nextBoolean()) 1 else -1
            val wrongAnswer = correctAnswer + offset
            if (wrongAnswer >= 0 && wrongAnswer != correctAnswer) {
                choices.add(wrongAnswer)
            }
            attempts++
        }

        // Fallback: if we couldn't generate enough unique choices, fill with sequential values
        var fallback = 1
        while (choices.size < 4) {
            val candidate = correctAnswer + fallback
            if (candidate >= 0 && !choices.contains(candidate)) {
                choices.add(candidate)
            }
            fallback++
        }

        return choices.toList().shuffled()
    }
}
