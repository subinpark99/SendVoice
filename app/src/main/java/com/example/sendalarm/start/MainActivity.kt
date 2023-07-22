package com.example.sendalarm.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.sendalarm.R
import com.example.sendalarm.databinding.ActivityMainBinding
import com.example.sendalarm.databinding.ActivitySplashBinding
import com.example.sendalarm.home.HomeFragment
import com.example.sendalarm.setting.SettingFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity :AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {

        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragment(HomeFragment())
        viewPagerAdapter.addFragment(SettingFragment())

        //Adapter 연결
        binding.viewpager2.apply {
            adapter = viewPagerAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
        }

        //ViewPager, TabLayout 연결
        TabLayoutMediator(binding.tabLayout, binding.viewpager2) { tab, position ->
            Log.e("YMC", "ViewPager position: $position")
            when (position) {
                0 -> tab.setIcon(R.drawable.icon_home)
                1 -> tab.setIcon(R.drawable.icon_setting)
            }
        }.attach()
    }
}