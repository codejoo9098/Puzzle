package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class UpdateLikeStatusUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): APIResponse<VideoInfoEntity> = videoRepository.updateLikeStatus(documentInfo, uid, isLiked)
}