package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.app.util.SortType
import javax.inject.Inject

class GetSearchedSocialVideoListUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        index: Int,
        keyword: String,
        order: SortType,
        latestData: Long? = null
    ): APIResponse<List<VideoInfoEntity>> =
        videoRepository.getSearchedSocialVideoList(index, order, keyword, latestData)
}