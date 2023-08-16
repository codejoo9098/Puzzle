package com.juniori.puzzle.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.ActivityUpdateNicknameBinding
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.ui.common_ui.StateManager
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
                stateManager.showLoadingDialog()

                when(result) {
                    is TempAPIResponse.Success<UserInfoEntity> -> {
                        stateManager.dismissLoadingDialog()
                        finish()
                    }
                    is TempAPIResponse.Failure -> {
                        stateManager.dismissLoadingDialog()
                        Toast.makeText(this@UpdateNicknameActivity, getString(R.string.nickname_change_impossible), Toast.LENGTH_SHORT).show()
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