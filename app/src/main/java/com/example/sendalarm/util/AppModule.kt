package com.example.sendalarm.util

import android.content.Context
import android.content.SharedPreferences
import com.example.sendalarm.data.repository.RecordRepository
import com.example.sendalarm.data.repository.UserRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Hilt 모듈
@InstallIn(SingletonComponent::class) // Hilt 모듈을 설치할 안드로이드 컴포넌트
internal object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Singleton
    @Provides
    fun provideDynamicLink(): FirebaseDynamicLinks{
        return FirebaseDynamicLinks.getInstance()
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        auth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        fcm: FirebaseMessaging,
        dynamicLink: FirebaseDynamicLinks,
        context: Context
    ): UserRepository {
        return UserRepository(auth, fireStore, fcm, dynamicLink,context)
    }

    @Singleton
    @Provides
    fun provideSendVoiceRepository(
        auth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        fcm: FirebaseMessaging
    ): RecordRepository {
        return RecordRepository(auth,fireStore, fcm)
    }

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("SendVoice", Context.MODE_PRIVATE)

    @Provides
    fun providesApplicationContext(@ApplicationContext context: Context) = context
}
