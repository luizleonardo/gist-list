package com.example.gistlist

import android.app.Application
import com.example.gistlist.di.databaseModule
import com.example.gistlist.di.netWorkModules
import com.example.gistlist.di.repositoryModules
import com.example.gistlist.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        loadKoin()
    }

    private fun loadKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    netWorkModules,
                    repositoryModules,
                    viewModels
                )
            )
        }
    }
}