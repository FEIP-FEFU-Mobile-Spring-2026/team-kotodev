package ru.fefu.store

import android.app.Application
import ru.fefu.store.di.AppContainer
import ru.fefu.store.di.DefaultAppContainer

class StoreApplication : Application() {

    val appContainer: AppContainer by lazy {
        DefaultAppContainer(applicationContext)
    }
}