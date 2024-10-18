package com.juniori.puzzle.ui.mygallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.getVideoListMockData
import com.juniori.puzzle.ui.gallery.GalleryState
import com.juniori.puzzle.ui.gallery.mygallery.MyGalleryViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MyGalleryViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockUserEntity = UserInfoEntity("aaa", "nickname", "profile")
    private val mockVideoList = getVideoListMockData().map { it.copy(ownerUid = "aaa") }
    private val searchedVideoList = mockVideoList.filter { it.location.contains("서대문구") }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNormalFirstDataWithoutQueryAndPagingTest(): Unit = runTest {
        val mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        val mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        val mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(mockVideoList))
        Mockito.lenient().`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(APIResponse.Success(searchedVideoList))

        val mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()

        val resultList = mockMyGalleryViewModel.list.value
        assertEquals(mockVideoList, resultList)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNormalFirstDataWithQueryTest(): Unit = runTest {
        val mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        val mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        val mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(mockVideoList))
        Mockito.`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(APIResponse.Success(searchedVideoList))

        val mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)

        mockMyGalleryViewModel.setQueryText("서대문구")
        mockMyGalleryViewModel.getMyData().join()

        val resultList = mockMyGalleryViewModel.list.value
        assertEquals(searchedVideoList, resultList)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFirstDataWithoutUidTest(): Unit = runTest {
        val mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        val mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        val mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(mockVideoList))
        Mockito.lenient().`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(APIResponse.Success(searchedVideoList))
        val mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()

        assertEquals(GalleryState.NETWORK_ERROR_BASE, mockMyGalleryViewModel.state.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun emptyDataTest(): Unit = runTest {
        val mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        val mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        val mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(emptyList()))
        Mockito.lenient().`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(APIResponse.Success(emptyList()))
        val mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)
        mockMyGalleryViewModel.getMyData().join()

        assertEquals(emptyList<VideoInfoEntity>(), mockMyGalleryViewModel.list.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun failureDataTest(): Unit = runTest {
        val mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        val mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        val mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Failure(Exception()))
        Mockito.lenient().`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(APIResponse.Failure(Exception()))
        val mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()

        assertEquals(GalleryState.NETWORK_ERROR_BASE, mockMyGalleryViewModel.state.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}