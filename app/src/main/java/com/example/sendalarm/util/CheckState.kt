package com.example.sendalarm.util

sealed class CheckState {
    object SuccessState : CheckState()
    class FailureState(val error: String) : CheckState()
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String = ""
) {
    class Success<T>(data: T? = null, message: String = "") : Resource<T>(data, message)
    class Error<T>(message: String = "Error") : Resource<T>(message = message)
}