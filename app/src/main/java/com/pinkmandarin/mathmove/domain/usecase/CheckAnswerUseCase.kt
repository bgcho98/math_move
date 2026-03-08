package com.pinkmandarin.mathmove.domain.usecase

import com.pinkmandarin.mathmove.domain.model.MathProblem
import javax.inject.Inject

class CheckAnswerUseCase @Inject constructor() {

    operator fun invoke(problem: MathProblem, selectedAnswer: Int): Boolean {
        return selectedAnswer == problem.answer
    }
}
