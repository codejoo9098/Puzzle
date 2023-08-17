package com.juniori.puzzle.domain.repository

import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthRepository {
    fun getCurrentUserInfo(): MutableStateFlow<TempAPIResponse<UserInfoEntity>>
    suspend fun requestLogin(idToken: String): TempAPIResponse<UserInfoEntity>
    suspend fun requestLogout(): TempAPIResponse<Unit>
    suspend fun requestWithdraw(idToken: String): TempAPIResponse<Unit>
    suspend fun updateNickname(newNickname: String): TempAPIResponse<UserInfoEntity>
}
