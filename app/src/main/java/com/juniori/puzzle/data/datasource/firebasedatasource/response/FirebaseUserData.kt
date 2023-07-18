package com.juniori.puzzle.data.datasource.firebasedatasource.response

import com.google.gson.annotations.SerializedName

data class FirebaseUserItem(
    @SerializedName("name") val uid: String,
    @SerializedName("fields") val firebaseUserDetail: FirebaseUserDetail,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null
)

data class FirebaseUserDetail(
    @SerializedName("user_display_name") val nickname: StringValue,
    @SerializedName("profile_image") val profileImage: StringValue,
)
