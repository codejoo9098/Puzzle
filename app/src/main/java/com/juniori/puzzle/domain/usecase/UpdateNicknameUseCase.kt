package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val videoRepository: VideoRepository
){
    suspend operator fun invoke(newNickname: String): APIResponse<UserInfoEntity> {
        if (newNickname.isBlank()) {
            return APIResponse.Failure(Exception())
        }

        val newInfo = withContext(Dispatchers.IO) {
            authRepository.updateNickname(newNickname)
        }

        return if (newInfo is APIResponse.Success) {
            videoRepository.updateServerNickname(newInfo.result)
        } else {
            APIResponse.Failure(Exception())
        }
    }
}