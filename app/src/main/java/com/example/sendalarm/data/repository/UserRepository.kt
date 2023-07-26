package com.example.sendalarm.data.repository

import android.content.Context
import android.util.Log
import com.example.sendalarm.data.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val fcm: FirebaseMessaging,
    private val context: Context
) {

   private fun currentUid()=auth.currentUser!!.uid

    fun loginWithKakao(
        success: (User) -> Unit,
        failure: (String) -> Unit
    ) {

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("LOGIN", "카카오 계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("LOGIN", "카카오 계정으로 로그인 성공")

                UserApiClient.instance.me { user, _ ->
                    val nickname = user?.kakaoAccount?.profile?.nickname.toString()
                    val email = user?.kakaoAccount?.email.toString()

                    signUpWithFirebase(token.accessToken, nickname, email, success, failure)
                }

            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오톡으로 로그인 성공")
                    UserApiClient.instance.me { user, _ ->
                        val nickname = user?.kakaoAccount?.profile?.nickname.toString()
                        val email = user?.kakaoAccount?.email.toString()

                        signUpWithFirebase(token.accessToken, nickname, email, success, failure)
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun signUpWithFirebase(
        token: String,
        nickname: String,
        email: String,
        success: (User) -> Unit,
        failure: (String) -> Unit
    ) {

        auth.signInWithCustomToken(token)
            .addOnSuccessListener {
                getFcmToken(
                    {
                        val userId = auth.uid.toString()
                        val user = User(userId, it, nickname, email)
                        success(user)
                        fireStore.collection("User").document(email).set(user)
                    },
                    failure = { failure(it) },
                )
            }
            .addOnFailureListener {
                failure(it.toString())
                Log.d("createUser", it.toString())
            }
    }


    private fun getFcmToken(
        success: (String) -> Unit,
        failure: (String) -> Unit
    ) {
        fcm.token.addOnSuccessListener {
            success(it)
        }.addOnFailureListener {
            failure(it.toString())
        }
    }

    fun newToken(success: (String) -> Unit, failure: (String) -> Unit) {
        fcm.token.addOnSuccessListener {
            val token = it
            success(token)
        }.addOnFailureListener { exception ->
            failure(exception.toString())
        }
    }


    fun updateFcmToken(newToken: String) {
        fireStore.collection("users").document(currentUid()).update("token", newToken)
    }


}






