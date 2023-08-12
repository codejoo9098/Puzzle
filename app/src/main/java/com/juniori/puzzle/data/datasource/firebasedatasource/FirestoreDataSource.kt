package com.juniori.puzzle.data.datasource.firebasedatasource

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.firebasedatasource.response.ArrayValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.BooleanValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.IntegerValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.RunQueryRequestDTO
import com.juniori.puzzle.data.datasource.firebasedatasource.response.StringValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.StringValues
import com.juniori.puzzle.data.datasource.firebasedatasource.response.FirebaseUserDetail
import com.juniori.puzzle.data.datasource.firebasedatasource.response.VideoDetail
import com.juniori.puzzle.data.converter.toVideoInfoEntityList
import com.juniori.puzzle.data.datasource.firebasedatasource.response.toStringValues
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.app.util.GCS_OPEN_URL
import com.juniori.puzzle.domain.customtype.GallerySortType
import com.juniori.puzzle.app.util.extensions.toLocationKeyword
import com.juniori.puzzle.data.converter.toUserInfoEntity
import com.juniori.puzzle.data.converter.toVideoInfoEntity
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import java.io.IOException
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val service: FirestoreService
) {
    suspend fun deleteVideoItem(documentId: String): APIResponse<Unit> {
        return try {
            APIResponse.Success(service.deleteVideoItemDocument(documentId))
        } catch (e: Exception) {
            APIResponse.Failure(e)
        }
    }

    suspend fun changeVideoItemPrivacy(
        documentInfo: VideoInfoEntity
    ): APIResponse<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate.not()),
                            likeCount = IntegerValue(likedCount.toLong()),
                            likedUserList = ArrayValue(likedUserUidList.toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                APIResponse.Success(it.toVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun addVideoItemLike(
        documentInfo: VideoInfoEntity,
        uid: String
    ): APIResponse<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate),
                            likeCount = IntegerValue(likedCount.toLong() + 1),
                            likedUserList = ArrayValue((likedUserUidList + uid).toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                APIResponse.Success(it.toVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun removeVideoItemLike(
        documentInfo: VideoInfoEntity,
        uid: String
    ): APIResponse<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate),
                            likeCount = IntegerValue(likedCount.toLong() - 1),
                            likedUserList = ArrayValue((likedUserUidList - uid).toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                APIResponse.Success(it.toVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun postVideoItem(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String
    ): APIResponse<VideoInfoEntity> {
        return try {
            service.createVideoItemDocument(
                videoName,
                mapOf(
                    "fields" to VideoDetail(
                        ownerUid = StringValue(uid),
                        videoUrl = StringValue(GCS_OPEN_URL + "video/" + videoName),
                        thumbUrl = StringValue(GCS_OPEN_URL + "thumb/" + videoName),
                        isPrivate = BooleanValue(isPrivate),
                        likeCount = IntegerValue(0),
                        likedUserList = ArrayValue(StringValues(listOf())),
                        updateTime = IntegerValue(System.currentTimeMillis()),
                        location = StringValue(location),
                        locationKeyword = ArrayValue(location.toLocationKeyword().toStringValues()),
                        memo = StringValue(memo)
                    )
                )
            ).let {
                APIResponse.Success(it.toVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun getMyVideoItems(
        uid: String,
        offset: Int? = null,
        limit: Int? = null
    ): APIResponse<List<VideoInfoEntity>> {
        return try {
            APIResponse.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        FirebaseQueryFactory.getMyVideoQuery(uid, offset, limit)
                    )
                ).toVideoInfoEntityList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun getMyVideoItemsWithKeyword(
        uid: String,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?
    ): APIResponse<List<VideoInfoEntity>> {
        return try {
            APIResponse.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        FirebaseQueryFactory.getMyVideoWithKeywordQuery(uid, toSearch, keyword, offset, limit)
                    )
                ).toVideoInfoEntityList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderBy(
        orderBy: GallerySortType,
        latestData: Long?,
        offset: Int? = null,
        limit: Int? = null,
    ): APIResponse<List<VideoInfoEntity>> {
        return try {
            APIResponse.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        FirebaseQueryFactory.getPublicVideoQuery(
                            orderBy = orderBy,
                            latestData = latestData,
                            offset = offset,
                            limit = limit
                        )
                    )
                ).toVideoInfoEntityList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsWithKeywordOrderBy(
        orderBy: GallerySortType,
        toSearch: String,
        keyword: String,
        latestData: Long?,
        offset: Int? = null,
        limit: Int? = null
    ): APIResponse<List<VideoInfoEntity>> {
        return try {
            APIResponse.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        FirebaseQueryFactory.getPublicVideoWithKeywordQuery(
                            orderBy,
                            toSearch,
                            keyword,
                            latestData,
                            offset,
                            limit,
                        )
                    )
                ).toVideoInfoEntityList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun getUserItem(
        uid: String
    ): APIResponse<UserInfoEntity> {
        return try {
            service.getUserItemDocument(uid).let {
                APIResponse.Success(it.toUserInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun postUserItem(
        uid: String,
        nickname: String,
        profileImage: String
    ): APIResponse<UserInfoEntity> {
        return try {
            service.createUserItemDocument(
                uid,
                mapOf(
                    "fields" to FirebaseUserDetail(
                        nickname = StringValue(nickname),
                        profileImage = StringValue(profileImage)
                    )
                )
            ).let {
                APIResponse.Success(it.toUserInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            APIResponse.Failure(e)
        }
    }

    suspend fun changeUserNickname(
        uid: String,
        newNickname: String,
        profileImage: String
    ): TempAPIResponse<UserInfoEntity> {
        return try {
            service.patchUserItemDocument(
                uid,
                mapOf(
                    "fields" to FirebaseUserDetail(
                        nickname = StringValue(newNickname),
                        profileImage = StringValue(profileImage)
                    )
                )
            ).let {
                TempAPIResponse.Success(it.toUserInfoEntity())
            }
        } catch (e: IOException) {
            TempAPIResponse.Failure(APIErrorType.NOT_CONNECTED)
        } catch (e: java.lang.Exception) {
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }
}
