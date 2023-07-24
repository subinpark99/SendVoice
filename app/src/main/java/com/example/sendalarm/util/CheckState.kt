package com.example.sendalarm.util

sealed class CheckState {
    object SuccessState : CheckState()
    class FailureState(val error: String) : CheckState()
}