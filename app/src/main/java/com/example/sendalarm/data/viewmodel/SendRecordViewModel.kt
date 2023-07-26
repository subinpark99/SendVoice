package com.example.sendalarm.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sendalarm.data.repository.SendRecordRepository
import com.example.sendalarm.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SendRecordViewModel @Inject constructor(private val repo: SendRecordRepository) :
    ViewModel() {

    }
