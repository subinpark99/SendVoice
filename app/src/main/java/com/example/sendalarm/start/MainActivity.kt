package com.example.sendalarm.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sendalarm.R
import com.example.sendalarm.recordlist.RecordListFragment
import com.example.sendalarm.databinding.ActivityMainBinding
import com.example.sendalarm.home.HomeFragment
import com.example.sendalarm.setting.SettingFragment
import com.example.sendalarm.util.MyApplication
import com.example.sendalarm.util.Preference
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val s=preference.getUser()
        Log.d("s",s.toString())


        initViewPager()
    }

    private fun initViewPager() {

        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragment(HomeFragment())
        viewPagerAdapter.addFragment(RecordListFragment())
        viewPagerAdapter.addFragment(SettingFragment())

        //Adapter 연결
        binding.viewpager2.adapter = viewPagerAdapter


        //ViewPager, TabLayout 연결
        TabLayoutMediator(binding.tabLayout, binding.viewpager2) { tab, position ->
            Log.e("YMC", "ViewPager position: $position")
            when (position) {
                0 -> tab.setIcon(R.drawable.icon_home)
                1 -> tab.setIcon(R.drawable.icon_record_list)
                2 -> tab.setIcon(R.drawable.icon_setting)
            }
        }.attach()
    }
}