package com.juniori.puzzle.ui.gallery.mygallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.ui.gallery.GalleryState
import com.juniori.puzzle.domain.constant.PagingConst.ITEM_CNT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    val getMyVideoListUseCase: GetMyVideoListUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val getSearchedMyVideoUseCase: GetSearchedMyVideoUseCase
) : ViewModel() {
    private val _list = MutableStateFlow<List<VideoInfoEntity>>(emptyList())
    val list: StateFlow<List<VideoInfoEntity>>
        get() = _list

    private val _refresh = MutableStateFlow(false)
    val refresh: StateFlow<Boolean>
        get() = _refresh

    private val _state = MutableStateFlow(GalleryState.NONE)
    val state: StateFlow<GalleryState>
        get() = _state

    private var query = ""
    private var pagingEndFlag = false

    fun setQueryText(nowQuery: String?) {
        if (query == nowQuery) {
            return
        }
        query = if (nowQuery != null && nowQuery.isNotBlank()) {
            nowQuery
        } else {
            ""
        }

        getMyData()
    }

    private fun getQueryData() = viewModelScope.launch {
        if (refresh.value == true) {
            cancel()
            return@launch
        }
        val uid = getUid()

        _list.value = emptyList()
        pagingEndFlag = false

        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_BASE
        } else {
            _refresh.value = true
            val data = getSearchedMyVideoUseCase(uid, 0, query)
            if (data is APIResponse.Success) {
                _state.value = GalleryState.NONE

                val result = data.result
                if (result.isEmpty().not()) {
                    if (result.size < ITEM_CNT) {
                        pagingEndFlag = true
                    }
                    _list.value = result
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }

            _refresh.value = false
        }
    }

    private fun getBaseData() = viewModelScope.launch {
        if (refresh.value == true) {
            cancel()
            return@launch
        }
        val uid = getUid()

        _list.value = emptyList()
        pagingEndFlag = false

        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_BASE
        } else {
            _refresh.value = true
            val data = getMyVideoListUseCase(uid, 0)
            if (data is APIResponse.Success) {
                _state.value = GalleryState.NONE

                val result = data.result
                if (result.isEmpty().not()) {
                    if (result.size < ITEM_CNT) {
                        pagingEndFlag = true
                    }
                    _list.value = result
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }

            _refresh.value = false
        }
    }

    fun getPaging(start: Int) = viewModelScope.launch {
        if (refresh.value == true || pagingEndFlag) {
            cancel()
            return@launch
        }

        val uid = getUid()
        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_PAGING
        } else {
            _refresh.value = true
            val data = if (query.isBlank()) {
                getMyVideoListUseCase(uid, start)
            } else {
                getSearchedMyVideoUseCase(uid, start, query)
            }

            if (data is APIResponse.Success) {
                val result = data.result
                if (result.isEmpty()) {
                    _state.value = GalleryState.END_PAGING
                    delay(1000)
                    _state.value = GalleryState.NONE
                    pagingEndFlag = true
                } else {
                    _state.value = GalleryState.NONE
                    addItems(result)
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_PAGING
            }

            _refresh.value = false
        }
    }

    fun getMyData() = viewModelScope.launch {
        if (query.isEmpty()) {
            getBaseData()
        } else {
            getQueryData()
        }
    }

    private fun getUid(): String? {
        val userInfo = getUserInfoUseCase().value
        val uid: String? = if (userInfo is TempAPIResponse.Success) {
            userInfo.data.uid
        } else {
            null
        }

        return uid
    }

    private fun addItems(items: List<VideoInfoEntity>) {
        val newList = mutableListOf<VideoInfoEntity>()
        _list.value?.forEach {
            newList.add(it)
        }

        items.forEach {
            newList.add(it)
        }

        _list.value = newList
    }
}
