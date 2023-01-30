package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class PostVideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String,
        videoByteArray: ByteArray,
        imageByteArray: ByteArray
    ): APIResponse<VideoInfoEntity> = videoRepository.uploadVideo(
        uid, videoName, isPrivate, location, memo, videoByteArray, imageByteArray
    )
}
