package com.juniori.puzzle.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.ui.MainActivity
import com.juniori.puzzle.R
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.databinding.ActivityUpdateNicknameBinding
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.app.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateNicknameActivity : AppCompatActivity() {
    private val binding: ActivityUpdateNicknameBinding by lazy { ActivityUpdateNicknameBinding.inflate(layoutInflater) }
    private val viewModel: UpdateNicknameViewModel by viewModels()
    private var currentNickname = ""
    @Inject lateinit var stateManager: StateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        stateManager.createLoadingDialog(binding.viewContainer)

        lifecycleScope.launchWhenStarted {
            viewModel.finalUserInfo.collect { result ->
                when(result) {
                    is APIResponse.Success<UserInfoEntity> -> {
                        stateManager.dismissLoadingDialog()

                        val intent = Intent(this@UpdateNicknameActivity, MainActivity::class.java).apply {
                            putExtra(MyPageFragment.NEW_NICKNAME, currentNickname)
                        }

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    is APIResponse.Failure -> {
                        stateManager.dismissLoadingDialog()
                        Toast.makeText(this@UpdateNicknameActivity, getString(R.string.nickname_change_impossible), Toast.LENGTH_SHORT).show()
                    }
                    is APIResponse.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                }
            }
        }

        binding.completeButton.setOnClickListener {
            currentNickname = binding.nicknameContainer.text.toString()
            viewModel.updateUserInfo(currentNickname)
        }
    }
}