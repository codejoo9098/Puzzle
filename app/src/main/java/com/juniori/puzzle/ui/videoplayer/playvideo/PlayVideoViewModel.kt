package com.juniori.puzzle.ui.videoplayer.playvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.ChangeVideoScopeUseCase
import com.juniori.puzzle.domain.usecase.DeleteVideoUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoByUidUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.UpdateLikeStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikeStatusUseCase: UpdateLikeStatusUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val changeVideoScopeUseCase: ChangeVideoScopeUseCase,
    private val getUserInfoByUidUseCase: GetUserInfoByUidUseCase
) : ViewModel() {
    private val _getLoginInfoFlow = MutableStateFlow<APIResponse<UserInfoEntity>?>(null)
    val getLoginInfoFlow: StateFlow<APIResponse<UserInfoEntity>?> = _getLoginInfoFlow

    private val _getPublisherInfoFlow = MutableStateFlow<APIResponse<UserInfoEntity>?>(null)
    val getPublisherInfoFlow: StateFlow<APIResponse<UserInfoEntity>?> = _getPublisherInfoFlow

    private val _deleteFlow = MutableStateFlow<APIResponse<Unit>?>(null)
    val deleteFlow: StateFlow<APIResponse<Unit>?> = _deleteFlow

    private val _videoFlow = MutableStateFlow<APIResponse<VideoInfoEntity>?>(null)
    val videoFlow: StateFlow<APIResponse<VideoInfoEntity>?> = _videoFlow

    private val _likeState = MutableStateFlow(false)
    val likeState: StateFlow<Boolean>
        get() = _likeState

    init {
        _getLoginInfoFlow.value = getUserInfoUseCase()
    }

    fun initVideoFlow(currentVideo: VideoInfoEntity) {
        viewModelScope.launch {
            _videoFlow.emit(APIResponse.Success(currentVideo))
        }
    }

    fun getPublisherInfo(uid: String) {
        viewModelScope.launch {
            _getPublisherInfoFlow.value = getUserInfoByUidUseCase(uid)
        }
    }

    fun setCurrentLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            _likeState.emit(currentVideo.likedUserUidList.contains(currentUid))
        }
    }

    fun changeLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            _videoFlow.emit(APIResponse.Loading)
            val result = updateLikeStatusUseCase(currentVideo, currentUid, likeState.value)
            if (result is APIResponse.Success) {
                _videoFlow.emit(result)
                _likeState.emit(likeState.value.not())
            }
        }
    }

    fun deleteVideo(documentId: String) = viewModelScope.launch {
        _deleteFlow.emit(APIResponse.Loading)
        _deleteFlow.emit(deleteVideoUseCase(documentId))
    }

    fun updateVideoPrivacy(documentInfo: VideoInfoEntity) = viewModelScope.launch {
        _videoFlow.emit(APIResponse.Loading)
        _videoFlow.emit(changeVideoScopeUseCase(documentInfo))
    }
}