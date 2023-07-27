package com.example.sendalarm.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sendalarm.data.entity.User
import com.example.sendalarm.data.viewmodel.UserViewModel
import com.example.sendalarm.databinding.ActivitySplashBinding
import com.example.sendalarm.util.Preference
import com.example.sendalarm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: UserViewModel by viewModels()

    @Inject
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        autoLogin()
    }

    private fun autoLogin() {

        val flag = preference.getAutoLogin()

        if (flag) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {

        binding.kakaoLoginIv.setOnClickListener {
            viewModel.loginWithKakao()
        }

        viewModel.signInState.observe(this) {
            when (it) {
                is Resource.Success -> {
                    saveUser(it.data!!)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                is Resource.Error -> Log.d("Login", it.message)
            }
        }
    }

    private fun saveUser(user: User) {

        preference.setUser(user)
        preference.setAutoLogin(true)  // 자동 로그인

    }

    private fun init() {

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


}