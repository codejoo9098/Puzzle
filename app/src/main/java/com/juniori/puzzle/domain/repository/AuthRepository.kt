package com.juniori.puzzle.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthRepository {
    fun getCurrentUserInfo(): MutableStateFlow<TempAPIResponse<UserInfoEntity>>
    suspend fun requestLogin(idToken: String): TempAPIResponse<UserInfoEntity>
    suspend fun requestLogout(): APIResponse<Unit>
    suspend fun requestWithdraw(idToken: String): APIResponse<Unit>
    suspend fun updateNickname(newNickname: String): TempAPIResponse<UserInfoEntity>
}
