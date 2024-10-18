package com.juniori.puzzle.ui.mygallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.getVideoListMockData
import com.juniori.puzzle.ui.gallery.mygallery.MyGalleryViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MyGalleryPagingTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockUserEntity = UserInfoEntity("aaa", "nickname", "profile")
    private val firstVideoList = getVideoListMockData().map { it.copy(ownerUid = "aaa") }
    private val extraList = getVideoListMockData().map { it.copy(documentId = "NewList", ownerUid = "aaa") }.subList(0, 6)

    lateinit var mockGetMyVideoListUseCase: GetMyVideoListUseCase
    lateinit var mockGetUserInfoUseCase: GetUserInfoUseCase
    lateinit var mockGetSearchedMyVideoUseCase: GetSearchedMyVideoUseCase
    lateinit var mockMyGalleryViewModel: MyGalleryViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
        Dispatchers.setMain(testDispatcher)

        mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun endPagingTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(emptyList()))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        mockMyGalleryViewModel.getPaging(1).join()

        Assert.assertEquals(firstVideoList, mockMyGalleryViewModel.list.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun singlePagingTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        mockMyGalleryViewModel.getPaging(1).join()

        Assert.assertEquals(firstVideoList + extraList, mockMyGalleryViewModel.list.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun multiPagingTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 4)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 5)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(4).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(5).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(66, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shortPagingDataTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(APIResponse.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(36, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shortFirstDataTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(extraList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(APIResponse.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(6, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingWithFailureTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1))
            .thenReturn(APIResponse.Failure(Exception()))
            .thenReturn(APIResponse.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(30, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingWithEmptyDataSameIndexTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1))
            .thenReturn(APIResponse.Success(emptyList()))
            .thenReturn(APIResponse.Success(extraList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(12, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingWithEmptyDataSkippingIndexTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(emptyList()))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(12, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingSkippingNumberTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))
        Mockito.lenient().`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 4)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(4).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(24, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingWithFirstEmptyDataTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(APIResponse.Success(emptyList()))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(18, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pagingWithFirstFailureTest(): Unit = runTest {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(MutableStateFlow(TempAPIResponse.Success(mockUserEntity)))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0))
            .thenReturn(APIResponse.Failure(Exception()))
            .thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(APIResponse.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(APIResponse.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getMyData().join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1).join()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2).join()
        delay(PAGING_DELAY)

        Assert.assertEquals(30, mockMyGalleryViewModel.list.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        const val PAGING_DELAY = 500L
    }
}