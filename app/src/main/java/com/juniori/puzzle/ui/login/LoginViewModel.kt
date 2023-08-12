package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.PostUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val requestLoginUseCase: RequestLoginUseCase,
    private val postUserInfoUseCase: PostUserInfoUseCase
) : ViewModel() {
    private val _loginFlow = getUserInfoUseCase()
    val loginFlow: StateFlow<TempAPIResponse<UserInfoEntity>?> = _loginFlow

    fun loginUser(account: GoogleSignInAccount) = viewModelScope.launch {
        val result = requestLoginUseCase(account).apply {
            if (this is TempAPIResponse.Success) {
                postUserInfoUseCase(data.uid, data.nickname, data.profileImage)
            }
        }

        _loginFlow.value = result
    }

}