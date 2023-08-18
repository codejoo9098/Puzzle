package com.juniori.puzzle.data.datasource.firebaseauth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.app.util.extensions.await
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    suspend fun requestLogin(idToken: String): TempAPIResponse<UserInfoEntity> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

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
        } catch (e: Exception) {
            e.printStackTrace()
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }

    fun requestLogout(): TempAPIResponse<Unit> {
        return try {
            firebaseAuth.signOut()

            TempAPIResponse.Success(Unit)
        } catch (exception: IOException) {
            TempAPIResponse.Failure(APIErrorType.NOT_CONNECTED)
        } catch (exception: Exception) {
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }

    suspend fun requestWithdraw(idToken: String): TempAPIResponse<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            suspendCancellableCoroutine { continuation ->
                firebaseAuth.currentUser?.reauthenticate(credential)
                    ?.addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    ?.addOnFailureListener {
                        continuation.resumeWithException(Exception())
                    } ?: continuation.resumeWithException(Exception())
            }

            suspendCancellableCoroutine { continuation ->
                firebaseAuth.currentUser?.delete()
                    ?.addOnSuccessListener {
                        continuation.resume(TempAPIResponse.Success(Unit))
                    }
                    ?.addOnFailureListener {
                        continuation.resumeWithException(Exception())
                    } ?: continuation.resumeWithException(Exception())
            }
        } catch (e: IOException) {
            TempAPIResponse.Failure(APIErrorType.NOT_CONNECTED)
        } catch (e: Exception) {
            TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }
}