package com.juniori.puzzle.domain.repository

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.app.util.SortType

interface VideoRepository {
    suspend fun getMyVideoList(uid: String, index: Int): APIResponse<List<VideoInfoEntity>>
    suspend fun getSearchedMyVideoList(uid: String, index: Int, keyword: String): APIResponse<List<VideoInfoEntity>>

    suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>>

    suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>>

    suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): APIResponse<VideoInfoEntity>

    suspend fun deleteVideo(documentId: String): APIResponse<Unit>
    suspend fun changeVideoScope(documentInfo: VideoInfoEntity): APIResponse<VideoInfoEntity>
    suspend fun uploadVideo(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String,
        videoByteArray: ByteArray,
        imageByteArray: ByteArray
    ): APIResponse<VideoInfoEntity>

    suspend fun getUserInfoByUidUseCase(uid: String): APIResponse<UserInfoEntity>
    suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): APIResponse<UserInfoEntity>

    suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): APIResponse<UserInfoEntity>
}