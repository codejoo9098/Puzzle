package com.juniori.puzzle.data.repositoryimpl

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.firebasedatasource.FirestoreDataSource
import com.juniori.puzzle.data.datasource.firebasedatasource.StorageDataSource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.domain.constant.PagingConst.ITEM_CNT
import com.juniori.puzzle.domain.customtype.GallerySortType
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val storageDataSource: StorageDataSource
) : VideoRepository {
    /** 내 비디오 목록 가져오기
     * @param uid: 사용자 uid
     * @param index: 가져오기 시작할 바디오 index*/
    override suspend fun getMyVideoList(uid: String, index: Int): APIResponse<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItems(
            uid = uid,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 검색을 통해 내 비디오 목록 가져오기
     * @param uid: 사용자 uid
     * @param index: 가져오기 시작할 바디오 index
     * @param keyword: 검색할 단어 */
    override suspend fun getSearchedMyVideoList(
        uid: String,
        index: Int,
        keyword: String
    ): APIResponse<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItemsWithKeyword(
            uid = uid,
            toSearch = "location_keyword",
            keyword = keyword,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 공개 상태인 비디오 목록 가져오기
     * @param index: 가져오기 시작할 바디오 index
     * @param gallerySortType: 정렬 타입 */
    override suspend fun getSocialVideoList(
        index: Int,
        gallerySortType: GallerySortType,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsOrderBy(
            orderBy = gallerySortType,
            latestData = latestData,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 검색을 통해 공개 상태인 비디오 목록 가져오기
     * @param index: 가져오기 시작할 바디오 index
     * @param gallerySortType: 정렬 타입
     * @param keyword: 검색할 단어*/
    override suspend fun getSearchedSocialVideoList(
        index: Int,
        gallerySortType: GallerySortType,
        keyword: String,
        latestData: Long?
    ): APIResponse<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsWithKeywordOrderBy(
            orderBy = gallerySortType,
            toSearch = "location_keyword",
            keyword = keyword,
            latestData = latestData,
            offset = index,
            limit = ITEM_CNT,
        )
    }

    override suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): APIResponse<UserInfoEntity> {
        return firestoreDataSource.changeUserNickname(
            userInfoEntity.uid,
            userInfoEntity.nickname,
            userInfoEntity.profileImage
        )
    }

    override suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): APIResponse<VideoInfoEntity> {
        return if (isLiked) {
            firestoreDataSource.removeVideoItemLike(documentInfo, uid)
        } else {
            firestoreDataSource.addVideoItemLike(documentInfo, uid)
        }
    }

    override suspend fun deleteVideo(documentId: String): APIResponse<Unit> {
        return if (storageDataSource.deleteVideo(documentId).isSuccess && storageDataSource.deleteThumbnail(
                documentId
            ).isSuccess
        ) {
            firestoreDataSource.deleteVideoItem(documentId)
        } else {
            APIResponse.Failure(Exception("delete video and thumbnail in Storage failed"))
        }
    }

    override suspend fun changeVideoScope(
        documentInfo: VideoInfoEntity
    ): APIResponse<VideoInfoEntity> {
        return firestoreDataSource.changeVideoItemPrivacy(documentInfo)
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
        return if (storageDataSource.insertVideo(
                videoName,
                videoByteArray
            ).isSuccess && storageDataSource.insertThumbnail(
                videoName,
                imageByteArray
            ).isSuccess
        ) {
            firestoreDataSource.postVideoItem(uid, videoName, isPrivate, location, memo)
        } else {
            APIResponse.Failure(Exception("upload video and thumbnail in Storage failed"))
        }
    }

    override suspend fun getUserInfoByUidUseCase(uid: String): APIResponse<UserInfoEntity> {
        return firestoreDataSource.getUserItem(uid)
    }

    override suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): APIResponse<UserInfoEntity> {
        return firestoreDataSource.postUserItem(uid, nickname, profileImage)
    }
}