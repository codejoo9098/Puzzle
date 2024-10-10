package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import com.juniori.puzzle.domain.usecase.RequestWithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestWithdrawUseCase: RequestWithdrawUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _requestLogoutFlow = MutableSharedFlow<APIResponse<Unit>>()
    val requestLogoutFlow: SharedFlow<APIResponse<Unit>> = _requestLogoutFlow

    private val _requestWithdrawFlow = MutableSharedFlow<APIResponse<Unit>>()
    val requestWithdrawFlow: SharedFlow<APIResponse<Unit>> = _requestWithdrawFlow

    private val _userNickname = MutableStateFlow("")
    val userNickname: StateFlow<String> = _userNickname

    private val _makeLogoutDialogFlow = MutableSharedFlow<Unit>()
    val makeLogoutDialogFlow: SharedFlow<Unit> = _makeLogoutDialogFlow

    private val _makeWithdrawDialogFlow = MutableSharedFlow<Unit>()
    val makeWithdrawDialogFlow: SharedFlow<Unit> = _makeWithdrawDialogFlow

    private val _navigateToUpdateNicknamePageFlow = MutableSharedFlow<Unit>()
    val navigateToUpdateNicknameFlow: SharedFlow<Unit> = _navigateToUpdateNicknamePageFlow

    init {
        updateUserInfo()
    }

    fun makeLogoutDialog() {
        viewModelScope.launch {
            _makeLogoutDialogFlow.emit(Unit)
        }
    }

    fun makeWithdrawDialog() {
        viewModelScope.launch {
            _makeWithdrawDialogFlow.emit(Unit)
        }
    }

    fun navigateToUpdateNicknamePage() {
        viewModelScope.launch {
            _navigateToUpdateNicknamePageFlow.emit(Unit)
        }
    }

    fun requestLogout() {
        viewModelScope.launch {
            _requestLogoutFlow.emit(APIResponse.Loading)
            withContext(Dispatchers.IO) {
                _requestLogoutFlow.emit(requestLogoutUseCase())
            }
        }
    }

    fun requestWithdraw(acct: GoogleSignInAccount) {
        viewModelScope.launch {
            _requestWithdrawFlow.emit(APIResponse.Loading)
            withContext(Dispatchers.IO) {
                _requestWithdrawFlow.emit(requestWithdrawUseCase(acct))
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            val data = getUserInfoUseCase()

            if (data is APIResponse.Success) {
                _userNickname.value = data.result.nickname
            }
            else {
                _userNickname.value = ""
            }
        }
    }

    fun updateUserNickname(newNickname: String) {
        _userNickname.value = newNickname
    }
}
