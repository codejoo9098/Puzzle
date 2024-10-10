package com.juniori.puzzle.data.converter

import com.juniori.puzzle.data.datasource.firebasedatasource.response.UserItem
import com.juniori.puzzle.domain.entity.UserInfoEntity

fun UserItem.toUserInfoEntity(): UserInfoEntity {
    return UserInfoEntity(
        uid.substringAfter("userReal/"),
        userDetail.nickname.stringValue,
        userDetail.profileImage.stringValue,
        null
    )
}