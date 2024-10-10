package com.juniori.puzzle.ui.mypage

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.domain.usecase.UpdateNicknameUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class UpdateNicknameUseCaseTest {
    lateinit var updateNicknameUseCase: UpdateNicknameUseCase
    lateinit var mockAuthRepository: AuthRepository
    lateinit var mockVideoRepository: VideoRepository

    private val testNickname = "K052"
    private val testUserInfoEntity = UserInfoEntity("UID", "K052", "profileImage")

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun normalUpdateUserInfoTest(): Unit = runBlocking {
        mockAuthRepository = Mockito.mock(AuthRepository::class.java)
        mockVideoRepository = Mockito.mock(VideoRepository::class.java)

        Mockito.`when`(mockAuthRepository.updateNickname(testNickname)).thenReturn(APIResponse.Success(testUserInfoEntity))
        Mockito.`when`(mockVideoRepository.updateServerNickname(testUserInfoEntity)).thenReturn(APIResponse.Success(testUserInfoEntity))

        updateNicknameUseCase = UpdateNicknameUseCase(mockAuthRepository, mockVideoRepository)
        assertEquals(APIResponse.Success(testUserInfoEntity) ,updateNicknameUseCase(testNickname))
    }

    @Test
    fun authFailUpdateUserInfoTest(): Unit = runBlocking {
        mockAuthRepository = Mockito.mock(AuthRepository::class.java)
        mockVideoRepository = Mockito.mock(VideoRepository::class.java)

        Mockito.`when`(mockAuthRepository.updateNickname(testNickname)).thenReturn(APIResponse.Failure(Exception()))
        Mockito.`when`(mockVideoRepository.updateServerNickname(testUserInfoEntity)).thenReturn(APIResponse.Success(testUserInfoEntity))

        updateNicknameUseCase = UpdateNicknameUseCase(mockAuthRepository, mockVideoRepository)
        assertTrue(updateNicknameUseCase(testNickname) is APIResponse.Failure)
    }

    @Test
    fun videoFailUpdateUserInfoTest(): Unit = runBlocking {
        mockAuthRepository = Mockito.mock(AuthRepository::class.java)
        mockVideoRepository = Mockito.mock(VideoRepository::class.java)

        Mockito.`when`(mockAuthRepository.updateNickname(testNickname)).thenReturn(APIResponse.Success(testUserInfoEntity))
        Mockito.`when`(mockVideoRepository.updateServerNickname(testUserInfoEntity)).thenReturn(APIResponse.Failure(Exception()))

        updateNicknameUseCase = UpdateNicknameUseCase(mockAuthRepository, mockVideoRepository)
        assertTrue(updateNicknameUseCase(testNickname) is APIResponse.Failure)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}