package com.juniori.puzzle.data.converter

import com.juniori.puzzle.data.datasource.firebasedatasource.response.FirebaseUserItem
import com.juniori.puzzle.domain.entity.UserInfoEntity

fun FirebaseUserItem.toUserInfoEntity(): UserInfoEntity {
    return UserInfoEntity(
        uid.substringAfter("userReal/"),
        firebaseUserDetail.nickname.stringValue,
        firebaseUserDetail.profileImage.stringValue,
        null
    )
}