package com.juniori.puzzle.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity

interface AuthRepository {
    fun getCurrentUserInfo(): APIResponse<UserInfoEntity>
    suspend fun requestLogin(acct: GoogleSignInAccount): APIResponse<UserInfoEntity>
    suspend fun requestLogout(): APIResponse<Unit>
    suspend fun requestWithdraw(acct: GoogleSignInAccount): APIResponse<Unit>
    suspend fun updateNickname(newNickname: String): APIResponse<UserInfoEntity>
}
