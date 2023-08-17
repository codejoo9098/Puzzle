package com.juniori.puzzle.data.repositoryimpl

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.firebaseauth.AuthDataSource
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override fun getCurrentUserInfo(): MutableStateFlow<TempAPIResponse<UserInfoEntity>> {
        userInfo.value = authDataSource.getCurrentUserInfo()
        return userInfo
    }

    override suspend fun updateNickname(newNickname: String): TempAPIResponse<UserInfoEntity> {
        val result = authDataSource.updateNickname(newNickname)
        userInfo.value = result
        return result
    }

    override suspend fun requestLogin(idToken: String) = authDataSource.requestLogin(idToken)

    override suspend fun requestLogout() = authDataSource.requestLogout()

    override suspend fun requestWithdraw(idToken: String) = authDataSource.requestWithdraw(idToken)

    companion object {
        val userInfo: MutableStateFlow<TempAPIResponse<UserInfoEntity>> = MutableStateFlow(TempAPIResponse.Failure(APIErrorType.NO_CONTENT))
    }
}
