package com.example.sendalarm.util.kakao

import com.example.sendalarm.util.MyApplication
import com.kakao.sdk.common.model.ApprovalType

class KakaoSDKAdapter(context: MyApplication) : KakaoSDKAdapter(){
    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig {
            MyApplication.instance?.getAppContext()
        }
    }

    override fun getSessionConfig() : ISessionConfig {
        return object : ISessionConfig{
            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_LOGIN_ALL)
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

            override fun isSecureMode(): Boolean {
                return true
            }

            override fun getApprovalType(): ApprovalType {
                return ApprovalType.INDIVIDUAL
            }

            override fun isSaveFormData(): Boolean {
                return true
            }

        }
    }
}