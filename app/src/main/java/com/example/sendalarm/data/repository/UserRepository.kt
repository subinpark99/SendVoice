package com.example.sendalarm.data.repository


import android.content.ContentValues.TAG
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sendalarm.data.entity.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import com.android.volley.Response
import com.example.sendalarm.BuildConfig
import javax.inject.Inject

open class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val fcm: FirebaseMessaging,
    private val context: Context
) {

    private fun Context.isKakaoTalkLoginAvailable(): Boolean {
        return UserApiClient.instance.isKakaoTalkLoginAvailable(this)
    }

    fun loginWithKakao(
        success: (User) -> Unit,
        failure: (String) -> Unit
    ) {
        if (context.isKakaoTalkLoginAvailable()) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                    // If KakaoTalk login fails, fall back to KakaoAccount login
                    loginWithKaKaoAccount(success, failure)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오톡으로 로그인 성공")
                    UserApiClient.instance.me { _, _ ->
                        getFirebaseJwt(token.accessToken).continueWith { task ->
                            val firebaseToken = task.result
                            signUpWithFirebase(firebaseToken!!, success, failure)
                        }
                    }
                }
            }
        } else {
            // KakaoTalk login is not available, perform KakaoAccount login
            loginWithKaKaoAccount(success, failure)
        }
    }

    private fun loginWithKaKaoAccount(success: (User) -> Unit, failure: (String) -> Unit) {

        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e("LOGIN", "카카오 계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("LOGIN", "카카오 계정으로 로그인 성공")

                UserApiClient.instance.me { _, _ ->
                    getFirebaseJwt(token.accessToken).continueWith { task ->
                        val firebaseToken = task.result
                        signUpWithFirebase(firebaseToken!!, success, failure)
                    }
                }
            }
        }
    }


    open fun getFirebaseJwt(kakaoAccessToken: String): Task<String> {
        Log.d(TAG, "LoginActivity - getFirebaseJwt() called")
        val source = TaskCompletionSource<String>()
        val queue = Volley.newRequestQueue(context)
        val url = "http://${BuildConfig.IPCONFIG}:8000/verifyToken" // validation server
        val validationObject: HashMap<String?, String?> = HashMap()
        validationObject["token"] = kakaoAccessToken

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url,
            JSONObject(validationObject as Map<*, *>),
            Response.Listener { response ->
                try {
                    val firebaseToken = response.getString("firebase_token")
                    source.setResult(firebaseToken)
                } catch (e: Exception) {
                    source.setException(e)
                }
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
                source.setException(error)
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = String.format(
                    "Basic %s", Base64.encodeToString(
                        String.format("%s:%s", "token", kakaoAccessToken)
                            .toByteArray(), Base64.DEFAULT
                    )
                )
                return params
            }
        }
        queue.add(request)
        return source.task // call validation server and retrieve firebase token
    }

    private fun signUpWithFirebase(
        token: String,
        success: (User) -> Unit,
        failure: (String) -> Unit
    ) {

        auth.signInWithCustomToken(token)
            .addOnSuccessListener {
                getFcmToken(
                    {
                        val userId = auth.uid.toString()
                        val nickname = auth.currentUser!!.displayName
                        val email = auth.currentUser!!.email
                        val user = User(userId, it, nickname!!, email!!)
                        success(user)
                        fireStore.collection("User").document(userId).set(user)
                    },
                    failure = { failure(it) },
                )
                Log.d("createUser", "Success")
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

    fun logout() {
        auth.signOut()
    }


    fun getUserList(userEmail: String): MutableLiveData<MutableList<User>?> {

        val userList = MutableLiveData<MutableList<User>?>()
        fireStore.collection("User")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = mutableListOf<User>()
                for (document in querySnapshot) {
                    val user = document.toObject(User::class.java)
                    //  if (user.email != userEmail)
                    users.add(user)
                }
                userList.value = users
            }
            .addOnFailureListener {
                userList.value = null
            }
        return userList
    }


}






