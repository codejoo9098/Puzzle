package com.juniori.puzzle.domain.usecase.common

import com.juniori.puzzle.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getCurrentUserInfo()
}