package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val videoRepository: VideoRepository
){
    suspend operator fun invoke(newNickname: String): TempAPIResponse<UserInfoEntity> {
        val newInfo = authRepository.updateNickname(newNickname)

        return if (newInfo is TempAPIResponse.Success) {
            videoRepository.updateServerNickname(newInfo.data)
        } else {
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }
}