package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.mypage.RequestLogoutUseCase
import com.juniori.puzzle.domain.usecase.mypage.RequestWithdrawUseCase
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
    private val _requestLogoutFlow = MutableSharedFlow<TempAPIResponse<Unit>>()
    val requestLogoutFlow: SharedFlow<TempAPIResponse<Unit>> = _requestLogoutFlow

    private val _requestWithdrawFlow = MutableSharedFlow<TempAPIResponse<Unit>>()
    val requestWithdrawFlow: SharedFlow<TempAPIResponse<Unit>> = _requestWithdrawFlow

    private val _currentUserInfo = getUserInfoUseCase()
    val currentUserInfo: StateFlow<TempAPIResponse<UserInfoEntity>> = _currentUserInfo

    private val _makeLogoutDialogFlow = MutableSharedFlow<Unit>()
    val makeLogoutDialogFlow: SharedFlow<Unit> = _makeLogoutDialogFlow

    private val _makeWithdrawDialogFlow = MutableSharedFlow<Unit>()
    val makeWithdrawDialogFlow: SharedFlow<Unit> = _makeWithdrawDialogFlow

    private val _navigateToUpdateNicknamePageFlow = MutableSharedFlow<Unit>()
    val navigateToUpdateNicknameFlow: SharedFlow<Unit> = _navigateToUpdateNicknamePageFlow

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
            withContext(Dispatchers.IO) {
                _requestLogoutFlow.emit(requestLogoutUseCase())
            }
        }
    }

    fun requestWithdraw(idToken: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _requestWithdrawFlow.emit(requestWithdrawUseCase(idToken))
            }
        }
    }
}
