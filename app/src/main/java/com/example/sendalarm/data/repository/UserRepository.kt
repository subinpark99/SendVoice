package com.example.sendalarm.data.repository


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
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
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.*
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


    @SuppressLint("SuspiciousIndentation")
    fun getUserList(userEmail: String): MutableLiveData<MutableList<User>?> {

        val userList = MutableLiveData<MutableList<User>?>()
        fireStore.collection("User")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = mutableListOf<User>()
                for (document in querySnapshot) {
                    val user = document.toObject(User::class.java)
                    // if (user.email != userEmail)
                    users.add(user)
                }
                userList.value = users
            }
            .addOnFailureListener {
                userList.value = null
            }
        return userList
    }

    fun sendKakaoLink(username: String) {

        val defaultText = TextTemplate(
            text = "${username}님이 친구추가를 요청하셨습니다!",

            link = Link(
                webUrl = "https://sendalarm.com",
                mobileWebUrl = "https://sendalarm.com"
            )
        )

        // 피드 메시지 보내기

        if (context.let { LinkClient.instance.isKakaoLinkAvailable(it) }) {
            // 카카오톡으로 카카오링크 공유 가능
            context.let {
                LinkClient.instance.defaultTemplate(it, defaultText) { linkResult, error ->
                    if (error != null) {
                        Log.e("TAG", "카카오링크 보내기 실패", error)
                    } else if (linkResult != null) {
                        Log.e("TAG", "카카오링크 보내기 성공 ${linkResult.intent}")
                        linkResult.intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(linkResult.intent) // 카카오톡이 깔려있을 경우 카카오톡으로 넘기기

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않음
                        Log.e("TAG", "Warning Msg: ${linkResult.warningMsg}")
                        Log.e("TAG", "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            }

        } else {  // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultText)

            // CustomTabs으로 디바이스 기본 브라우저 열기
            try {

                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.intent.flags = FLAG_ACTIVITY_NEW_TASK
                context.let { customTabsIntent.launchUrl(it, sharerUrl) }
                Log.d("customTab", "기본 브라우저")

            } catch (e: ActivityNotFoundException) {
                // 인터넷 브라우저가 없을 때
                Toast.makeText(context, "chrome 또는 인터넷 브라우저를 설치해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

}






