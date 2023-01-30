package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
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
    private val _loginFlow = MutableStateFlow<APIResponse<UserInfoEntity>?>(null)
    val loginFlow: StateFlow<APIResponse<UserInfoEntity>?> = _loginFlow

    init {
        getUserInfoUseCase().let { currentUser ->
            _loginFlow.value = currentUser
        }
    }

    fun loginUser(account: GoogleSignInAccount) = viewModelScope.launch {
        _loginFlow.value = APIResponse.Loading
        val result = requestLoginUseCase(account).apply {
            if (this is APIResponse.Success) {
                postUserInfoUseCase(result.uid, result.nickname, result.profileImage)
            }
        }
        _loginFlow.value = result
    }

}