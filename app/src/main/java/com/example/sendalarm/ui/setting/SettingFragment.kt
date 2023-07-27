package com.example.sendalarm.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendalarm.data.viewmodel.UserViewModel
import com.example.sendalarm.databinding.FragmentSettingBinding
import com.example.sendalarm.ui.start.SplashActivity
import com.example.sendalarm.util.Preference
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preference: Preference

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterRv: UserListAdapter
    private var selectEmail = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        adapterRv = UserListAdapter()
        bind()

        onRecyclerView()
        binding.friendSearchView.setOnQueryTextListener(searchViewTextListener)
        setSearchView()

        return binding.root
    }

    private fun bind() {

        binding.usernameTv.text = preference.getUser()!!.userName
        binding.useremailTv.text = preference.getUser()!!.email

        binding.logoutTv.setOnClickListener {
            viewModel.logout()
            preference.setAutoLogin(false)

            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }



        binding.friendInviteBtn.setOnClickListener {

            if (selectEmail.isNotEmpty()) {
                val builder = AlertDialog.Builder(requireContext())
                builder
                    .setMessage("$selectEmail \n님을 초대하시겠습니까??")
                    .setPositiveButton(
                        "확인"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "취소"
                    ) { dialog, _ ->
                        dialog.dismiss()

                    }
                builder.create()
                builder.show()

            } else {
                Toast.makeText(context, "이메일을 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    private fun onRecyclerView() {

        binding.searchEmailRv.apply {
            adapter = adapterRv
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        val userEmail = preference.getUser()!!.email
        viewModel.getUserList(userEmail).observe(viewLifecycleOwner) {
            if (it != null) {
                adapterRv.getUserList(it)
            }
        }

        adapterRv.setItemClickListener(object : UserListAdapter.InContentInterface {
            override fun onUserClicked(clicked: Boolean, email: String) {
                if (clicked) {
                    selectEmail = email
                }
            }
        })
    }


    private fun setSearchView() {
        val searchView = binding.friendSearchView
        searchView.isSubmitButtonEnabled = true

    }

    private var searchViewTextListener: SearchView.OnQueryTextListener =

        object : SearchView.OnQueryTextListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(s: String): Boolean {
                adapterRv.filter.filter(s)

                if (adapterRv.itemCount == 0) {
                    Snackbar.make(binding.root, "검색 결과 없음.", Snackbar.LENGTH_SHORT).show()
                }

                return false
            }

            // 텍스트 입력, 수정 시에 호출
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(s: String): Boolean {

                adapterRv.filter.filter(s)
                return false
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}