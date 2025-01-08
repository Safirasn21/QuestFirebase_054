package com.example.firebasepam

import android.app.Application
import com.example.firebasepam.di.AppContainer
import com.example.firebasepam.di.MahasiswaContainer

class MahasiswaApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container= MahasiswaContainer()
    }
}