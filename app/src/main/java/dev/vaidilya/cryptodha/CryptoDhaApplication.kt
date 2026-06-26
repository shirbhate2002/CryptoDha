package dev.vaidilya.cryptodha

import android.app.Application

class CryptoDhaApplication: Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}