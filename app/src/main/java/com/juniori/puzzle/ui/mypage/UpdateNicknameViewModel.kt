package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.mypage.UpdateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateNicknameViewModel @Inject constructor(
    private val updateNicknameUseCase: UpdateNicknameUseCase
): ViewModel() {
    private val _finalUserInfo = MutableSharedFlow<TempAPIResponse<UserInfoEntity>>()
    val finalUserInfo: SharedFlow<TempAPIResponse<UserInfoEntity>> = _finalUserInfo

    fun updateUserInfo(newNickname: String) = viewModelScope.launch {
        _finalUserInfo.emit(updateNicknameUseCase(newNickname))
    }
}