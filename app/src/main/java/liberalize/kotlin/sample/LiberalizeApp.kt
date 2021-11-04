package liberalize.kotlin.sample

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import liberalize.java.backend.sdk.LiberalizeBE
import liberalize.java.backend.sdk.data.network.Environment

class LiberalizeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY)

        // Use Java Backend SDK for payment flow; set environment
        LiberalizeBE.getInstance().setEnvironment(Environment.STAGING)
        // set private key
        LiberalizeBE.getInstance().setPrivateKey("<private key>")
    }
}