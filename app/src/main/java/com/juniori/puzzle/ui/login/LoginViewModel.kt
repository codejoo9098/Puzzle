package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.PostUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun loginUser(idToken: String) = viewModelScope.launch {
        val response = requestLoginUseCase(idToken)
        if (response is TempAPIResponse.Success && postUserInfoUseCase(response.data.uid, response.data.nickname, response.data.profileImage) is APIResponse.Success) {
            _loginFlow.value = response
        }
        else {
            _loginFlow.value = TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
        }
    }

}