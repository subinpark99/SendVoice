package com.example.sendalarm.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sendalarm.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}