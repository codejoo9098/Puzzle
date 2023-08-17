package com.juniori.puzzle.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentMypageBinding
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.ui.adapter.setDisplayName
import com.juniori.puzzle.ui.login.LoginActivity
import com.juniori.puzzle.ui.common_ui.PuzzleDialog
import com.juniori.puzzle.ui.common_ui.StateManager
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    @Inject lateinit var stateManager: StateManager
    private val warningDialog: PuzzleDialog by lazy { PuzzleDialog(requireContext()) }

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(
            requireContext(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    private val withdrawLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                runCatching {
                    val idToken = task.getResult(ApiException::class.java).idToken ?: throw Exception()
                    viewModel.requestWithdraw(idToken)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        stateManager.createLoadingDialog(container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.requestLogoutFlow.collect { result ->
                stateManager.showLoadingDialog()

                when(result) {
                    is TempAPIResponse.Success -> {
                        stateManager.dismissLoadingDialog()
                        val intent = Intent(context, LoginActivity::class.java)
                        activity?.finishAffinity()
                        startActivity(intent)
                    }
                    is TempAPIResponse.Failure -> {
                        stateManager.dismissLoadingDialog()
                        showErrorToastMessage(result.errorType)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.requestWithdrawFlow.collect { result ->
                stateManager.showLoadingDialog()

                when(result) {
                    is TempAPIResponse.Success -> {
                        stateManager.dismissLoadingDialog()
                        activity?.finishAffinity()
                    }
                    is TempAPIResponse.Failure -> {
                        stateManager.dismissLoadingDialog()
                        showErrorToastMessage(result.errorType)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.makeLogoutDialogFlow.collect {
                makeLogoutDialog()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.makeWithdrawDialogFlow.collect {
                makeWithdrawDialog()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.navigateToUpdateNicknameFlow.collect {
                val intent = Intent(context, UpdateNicknameActivity::class.java)
                startActivity(intent)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.currentUserInfo.collect { userInfoEntity ->
                if (userInfoEntity is TempAPIResponse.Success) {
                    setDisplayName(binding.userNickname, userInfoEntity.data.nickname)
                }
                else {
                    setDisplayName(binding.userNickname, getString(R.string.cannot_find_user))
                }

            }
        }
    }

    private fun makeLogoutDialog() {
        warningDialog
            .buildAlertDialog({
                viewModel.requestLogout()
            },{

            }).setMessage(getString(R.string.logout_remind))
            .setTitle(getString(R.string.logout))
            .showDialog()
    }

    private fun makeWithdrawDialog() {
        warningDialog
            .buildAlertDialog({
                val signInIntent = googleSignInClient.signInIntent
                withdrawLauncher.launch(signInIntent)
            },{

            }).setMessage(getString(R.string.withdraw_remind))
            .setTitle(getString(R.string.withdraw))
            .showDialog()
    }

    private fun showErrorToastMessage(errorType: APIErrorType) {
        when(errorType) {
            APIErrorType.NOT_CONNECTED -> Toast.makeText(requireContext(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show()
            APIErrorType.NO_CONTENT -> Toast.makeText(requireContext(), getString(R.string.empty_data), Toast.LENGTH_SHORT).show()
            APIErrorType.SERVER_ERROR -> Toast.makeText(requireContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
