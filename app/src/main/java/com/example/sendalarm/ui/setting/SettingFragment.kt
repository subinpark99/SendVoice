package com.example.sendalarm.ui.setting


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sendalarm.data.viewmodel.UserViewModel
import com.example.sendalarm.databinding.FragmentSettingBinding
import com.example.sendalarm.ui.start.SplashActivity
import com.example.sendalarm.util.Preference
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
    //  private var selectEmail = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        adapterRv = UserListAdapter()
        bind()




        return binding.root
    }

    private fun bind() {

        val userEmail = preference.getUser()!!.email
        val userName = preference.getUser()!!.userName

        binding.useremailTv.text = userEmail
        binding.usernameTv.text = userName



        binding.logoutTv.setOnClickListener {
            viewModel.logout()
            preference.setAutoLogin(false)

            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }



        binding.friendInviteBtn.setOnClickListener {
            viewModel.sendKakaoLink(userName)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


// 이메일 검색 기능 보류(유저 이메일로 검색해서 카카오톡 메시지로 초대하기)
//        onRecyclerView()
//        binding.friendSearchView.setOnQueryTextListener(searchViewTextListener)
//        val searchView = binding.friendSearchView
//        searchView.isSubmitButtonEnabled = true

//    @SuppressLint("NotifyDataSetChanged")
//    private fun onRecyclerView() {
//
//        binding.searchEmailRv.apply {
//            adapter = adapterRv
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        }
//
//        val userEmail = preference.getUser()!!.email
//        viewModel.getUserList(userEmail).observe(viewLifecycleOwner) {
//            if (it != null) {
//                it.add(User("", "", "수현", "psb8909@naver.com"))
//                it.add(User("", "", "진우", "jinwoo@naver.com"))
//                it.add(User("", "", "진우", "jinwoo2@naver.com"))
//                it.add(User("", "", "진우", "jinwoo3@naver.com"))
//                adapterRv.getUserList(it)
//
//                Log.d("s",it.toString())
//            }
//        }
//
//        adapterRv.setItemClickListener(object : UserListAdapter.InContentInterface {
//            override fun onUserClicked(clicked: Boolean, email: String) {
//
//                selectEmail = if (clicked) {
//                    email
//                } else {
//                    ""
//                }
//            }
//        })
//    }
//    private var searchViewTextListener: SearchView.OnQueryTextListener =
//
//        object : SearchView.OnQueryTextListener {
//
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onQueryTextSubmit(s: String): Boolean {
//
//                adapterRv.filter.filter(s)
//
//                if (adapterRv.itemCount == 0) {
//                    Snackbar.make(binding.root, "검색 결과 없음.", Snackbar.LENGTH_SHORT).show()
//                }
//
//                return false
//            }
//
//            // 텍스트 입력, 수정 시에 호출
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onQueryTextChange(s: String): Boolean {
//
//                adapterRv.filter.filter(s)
//                return false
//            }
//        }