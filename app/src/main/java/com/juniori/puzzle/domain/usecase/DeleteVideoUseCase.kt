package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class DeleteVideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(documentId: String): APIResponse<Unit> =
        videoRepository.deleteVideo(documentId)
}
