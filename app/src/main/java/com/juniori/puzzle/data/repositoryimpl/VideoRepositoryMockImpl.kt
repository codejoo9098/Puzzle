package com.juniori.puzzle.data.repositoryimpl

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class VideoRepositoryMockImpl @Inject constructor(private val videoList: List<VideoInfoEntity>) :
    VideoRepository {
    override suspend fun getMyVideoList(uid: String, index: Int): APIResponse<List<VideoInfoEntity>> {
        return APIResponse.Success(
            videoList.filter { videoInfoEntity -> videoInfoEntity.ownerUid == uid }
        )
    }

    override suspend fun getSearchedMyVideoList(
        uid: String,
        index: Int,
        keyword: String
    ): APIResponse<List<VideoInfoEntity>> {
        return APIResponse.Success(
            videoList.filter { videoInfoEntity -> videoInfoEntity.ownerUid == uid }
                .filter { videoInfoEntity -> videoInfoEntity.location == keyword }
        )
    }

    override suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): APIResponse<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVideo(documentId: String): APIResponse<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeVideoScope(documentInfo: VideoInfoEntity): APIResponse<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadVideo(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String,
        videoByteArray: ByteArray,
        imageByteArray: ByteArray
    ): APIResponse<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfoByUidUseCase(uid: String): APIResponse<UserInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): APIResponse<UserInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): APIResponse<UserInfoEntity> {
        TODO("Not yet implemented")
    }
}