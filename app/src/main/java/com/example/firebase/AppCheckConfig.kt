package com.example.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

private const val TAG = "AppCheck"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        FirebaseApp.initializeApp(context)
        val appCheck = FirebaseAppCheck.getInstance()

        val intentToken = intent?.getStringExtra("FIREBASE_APPCHECK_DEBUG_TOKEN")
        val buildConfigToken = try {
            val field = BuildConfig::class.java.getField("FIREBASE_APPCHECK_DEBUG_TOKEN")
            field.get(null) as? String
        } catch (e: Throwable) {
            null
        }

        val token = intentToken ?: buildConfigToken
        if (!token.isNullOrBlank()) {
            System.setProperty("firebase.appcheck.debug.token", token)
            Log.d(TAG, "Configured App Check debug token.")
        }

        appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        Log.d(TAG, "App Check initialized successfully.")
    } catch (e: Exception) {
        Log.e(TAG, "Error initializing App Check", e)
    }
}
