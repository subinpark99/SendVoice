package com.example.sendalarm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendalarm.data.entity.FriendList
import com.example.sendalarm.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        onRecyclerView()

        return binding.root
    }

    private fun onRecyclerView(){

        val rvAdapter= FriendListAdapter()
        binding.homeRv.adapter=rvAdapter
        binding.homeRv.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rvAdapter.sendItem(listOf(FriendList("sdf","수빈","sere@naver.com"),
            FriendList("seer","재현","sere@naver.com")
        ))

        rvAdapter.setItemClickListener(object : FriendListAdapter.ClickInterface {
            override fun onMemberClicked(friendUid: String) {

                DialogSendAlarm().show(parentFragmentManager,"sendAlarm")
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}