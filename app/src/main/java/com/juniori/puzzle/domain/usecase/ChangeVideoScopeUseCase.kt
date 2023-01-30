package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class ChangeVideoScopeUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(documentInfo: VideoInfoEntity): APIResponse<VideoInfoEntity> =
        videoRepository.changeVideoScope(documentInfo)
}
