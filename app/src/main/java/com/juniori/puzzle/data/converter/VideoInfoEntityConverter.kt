package com.juniori.puzzle.data.converter

import com.juniori.puzzle.data.datasource.firebasedatasource.response.RunQueryResponseDTO
import com.juniori.puzzle.data.datasource.firebasedatasource.response.VideoItem
import com.juniori.puzzle.domain.entity.VideoInfoEntity

fun List<RunQueryResponseDTO>.toVideoInfoEntityList(): List<VideoInfoEntity> =
    filter { it.videoItem != null }.map {
        it.videoItem?.toVideoInfoEntity()
            ?: throw Exception("getVideoItem from ResponseDTO Failed")
    }

fun VideoItem.toVideoInfoEntity(): VideoInfoEntity {
    return VideoInfoEntity(
        videoName.substringAfter("videoReal/"),
        videoDetail.ownerUid.stringValue,
        videoDetail.videoUrl.stringValue,
        videoDetail.thumbUrl.stringValue,
        videoDetail.isPrivate.booleanValue,
        videoDetail.likeCount.integerValue.toInt(),
        videoDetail.likedUserList.arrayValue.values?.map { it.stringValue } ?: listOf(),
        videoDetail.updateTime.integerValue,
        videoDetail.location.stringValue,
        videoDetail.locationKeyword.arrayValue.values?.map { it.stringValue } ?: listOf(),
        videoDetail.memo.stringValue
    )
}