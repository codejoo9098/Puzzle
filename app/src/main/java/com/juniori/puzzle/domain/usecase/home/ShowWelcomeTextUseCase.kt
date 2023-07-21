package com.juniori.puzzle.domain.usecase.home

import javax.inject.Inject
import kotlin.random.Random

class ShowWelcomeTextUseCase @Inject constructor() {
    private val random = Random(System.currentTimeMillis())

    operator fun invoke(welcomeTextArray: Array<String>): String = welcomeTextArray.random(random)
}