package com.juniori.puzzle.ui.login

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.PostUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLoginUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.Mockito

class LoginViewModelTest {
    lateinit var loginViewModel: LoginViewModel

    lateinit var mockAccount: GoogleSignInAccount
    lateinit var mockUserInfoEntity: UserInfoEntity

    lateinit var mockGetUserInfoUseCase: GetUserInfoUseCase
    lateinit var mockRequestLoginUseCase: RequestLoginUseCase
    lateinit var mockPostUserInfoUseCase: PostUserInfoUseCase

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockAccount = Mockito.mock(GoogleSignInAccount::class.java)
    }

    @Test
    fun normalLoginTest(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            mockUserInfoEntity = UserInfoEntity("UID", "Nickname", "ProfileImage")

            setupUseCase()

            Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(APIResponse.Failure(Exception()))
            Mockito.`when`(mockRequestLoginUseCase(mockAccount)).thenReturn(APIResponse.Success(mockUserInfoEntity))
            Mockito.`when`(mockPostUserInfoUseCase(mockUserInfoEntity.uid, mockUserInfoEntity.nickname, mockUserInfoEntity.profileImage)).thenReturn(APIResponse.Success(mockUserInfoEntity))

            loginViewModel = LoginViewModel(mockGetUserInfoUseCase, mockRequestLoginUseCase, mockPostUserInfoUseCase)
            loginViewModel.loginUser(mockAccount).join()

            assertEquals(APIResponse.Success(mockUserInfoEntity), loginViewModel.loginFlow.value)
        }
    }

    @Test
    fun requestLoginFailureTest(): Unit = runBlocking {
        mockUserInfoEntity = UserInfoEntity("UID", "Nickname", "ProfileImage")

        setupUseCase()

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(APIResponse.Failure(Exception()))
        Mockito.`when`(mockRequestLoginUseCase(mockAccount)).thenReturn(APIResponse.Failure(Exception()))
        Mockito.`when`(mockPostUserInfoUseCase(mockUserInfoEntity.uid, mockUserInfoEntity.nickname, mockUserInfoEntity.profileImage)).thenReturn(APIResponse.Success(mockUserInfoEntity))

        loginViewModel = LoginViewModel(mockGetUserInfoUseCase, mockRequestLoginUseCase, mockPostUserInfoUseCase)
        loginViewModel.loginUser(mockAccount).join()

        assertTrue(loginViewModel.loginFlow.value is APIResponse.Failure)
    }

    @Test
    fun postUserInfoFailureTest(): Unit = runBlocking {
        mockUserInfoEntity = UserInfoEntity("UID", "Nickname", "ProfileImage")

        setupUseCase()

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(APIResponse.Failure(Exception()))
        Mockito.`when`(mockRequestLoginUseCase(mockAccount)).thenReturn(APIResponse.Success(mockUserInfoEntity))
        Mockito.`when`(mockPostUserInfoUseCase(mockUserInfoEntity.uid, mockUserInfoEntity.nickname, mockUserInfoEntity.profileImage)).thenReturn(APIResponse.Failure(Exception()))

        loginViewModel = LoginViewModel(mockGetUserInfoUseCase, mockRequestLoginUseCase, mockPostUserInfoUseCase)
        loginViewModel.loginUser(mockAccount).join()

        assertTrue(loginViewModel.loginFlow.value is APIResponse.Failure)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    private fun setupUseCase() {
        mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        mockRequestLoginUseCase = Mockito.mock(RequestLoginUseCase::class.java)
        mockPostUserInfoUseCase = Mockito.mock(PostUserInfoUseCase::class.java)
    }
}