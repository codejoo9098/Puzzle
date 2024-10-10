package com.juniori.puzzle.data.datasource.firebaseauth

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.app.util.extensions.await
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import java.io.IOException
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun getCurrentUserInfo(): TempAPIResponse<UserInfoEntity> {
        return firebaseAuth.currentUser?.let { firebaseUser ->
            TempAPIResponse.Success(
                UserInfoEntity(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: "",
                    firebaseUser.photoUrl?.toString() ?: ""
                )
            )
        } ?: kotlin.run { TempAPIResponse.Failure(APIErrorType.NO_CONTENT) }
    }

    fun updateNickname(newNickname: String): TempAPIResponse<UserInfoEntity> {
        val newProfile = UserProfileChangeRequest.Builder()
            .setDisplayName(newNickname)
            .build()

        val result = firebaseAuth.currentUser?.updateProfile(newProfile)

        return result?.let {
            val uid = firebaseAuth.currentUser?.uid ?: ""
            val profileUrl = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
            val userInfoEntity = UserInfoEntity(uid, newNickname, profileUrl)

            TempAPIResponse.Success(userInfoEntity)
        } ?: kotlin.run { TempAPIResponse.Failure(APIErrorType.NO_CONTENT) }
    }

    suspend fun requestLogin(acct: GoogleSignInAccount): TempAPIResponse<UserInfoEntity> {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        return try {
            val result = firebaseAuth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                TempAPIResponse.Success(
                    UserInfoEntity(
                        firebaseUser.uid,
                        firebaseUser.displayName ?: "",
                        firebaseUser.photoUrl?.toString() ?: ""
                    )
                )
            }  ?: kotlin.run { TempAPIResponse.Failure(APIErrorType.NO_CONTENT) }
        } catch (e: IOException) {
            e.printStackTrace()
            TempAPIResponse.Failure(APIErrorType.NOT_CONNECTED)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }

    fun requestLogout(): APIResponse<Unit> {
        return try {
            firebaseAuth.signOut()

            APIResponse.Success(Unit)
        } catch (exception: Exception) {
            APIResponse.Failure(exception)
        }
    }

    suspend fun requestWithdraw(acct: GoogleSignInAccount): APIResponse<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            firebaseAuth.currentUser?.reauthenticate(credential)?.await()

            firebaseAuth.currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Withdrawal", "User account deleted.")
                    }
                    else {
                        Log.d("Withdrawal", "User account NOT deleted.")
                    }
                } ?: throw java.lang.Exception()

            APIResponse.Success(Unit)
        }
        catch (e: java.lang.Exception) {
            APIResponse.Failure(Exception())
        }
    }
}