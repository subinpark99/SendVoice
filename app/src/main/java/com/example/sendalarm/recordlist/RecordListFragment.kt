package com.example.sendalarm.recordlist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendalarm.data.entity.SendRecord
import com.example.sendalarm.databinding.FragmentRecordListBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordListFragment : Fragment() {
    private var _binding: FragmentRecordListBinding? = null
    private val binding get() = _binding!!


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecordListBinding.inflate(inflater, container, false)


        onRecyclerView()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun onRecyclerView(){

        val rvAdapter= RecordListAdapter()
        binding.recordListRv.adapter=rvAdapter
        binding.recordListRv.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val currentDate= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        rvAdapter.sendItem(listOf(SendRecord("s","수비니","머하노", null,currentDate)))

        rvAdapter.setItemClickListener(object : RecordListAdapter.ClickInterface{
            override fun onRecordClicked() {

            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}