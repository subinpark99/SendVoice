package com.example.sendalarm.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sendalarm.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RecordViewModel @Inject constructor(private val repo: RecordRepository) :
    ViewModel() {

    }
