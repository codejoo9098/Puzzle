package com.juniori.puzzle.data.datasource.firebasedatasource.response

import com.google.gson.annotations.SerializedName
import com.juniori.puzzle.domain.entity.UserInfoEntity

data class UserItem(
    @SerializedName("name") val uid: String,
    @SerializedName("fields") val userDetail: UserDetail,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null
)

data class UserDetail(
    @SerializedName("user_display_name") val nickname: StringValue,
    @SerializedName("profile_image") val profileImage: StringValue,
)
