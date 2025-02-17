package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class GetMyVideoListUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String, index: Int): APIResponse<List<VideoInfoEntity>> =
        videoRepository.getMyVideoList(uid, index)
}