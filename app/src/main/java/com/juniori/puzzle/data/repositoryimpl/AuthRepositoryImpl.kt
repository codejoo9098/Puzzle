package com.juniori.puzzle.data.repositoryimpl

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.datasource.firebaseauth.AuthDataSource
import com.juniori.puzzle.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun updateNickname(newNickname: String) = authDataSource.updateNickname(newNickname)

    override fun getCurrentUserInfo() = authDataSource.getCurrentUserInfo()

    override suspend fun requestLogin(acct: GoogleSignInAccount) = authDataSource.requestLogin(acct)

    override suspend fun requestLogout() = authDataSource.requestLogout()

    override suspend fun requestWithdraw(acct: GoogleSignInAccount) = authDataSource.requestWithdraw(acct)
}
