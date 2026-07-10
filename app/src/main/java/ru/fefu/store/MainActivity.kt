package ru.fefu.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.fefu.store.ui.theme.FEFUStoreTheme

class MainActivity : ComponentActivity() {

    private val appContainer
        get() = (application as StoreApplication).appContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FEFUStoreTheme {
                StoreApp(
                    catalogRepository = appContainer.catalogRepository,
                    cartRepository = appContainer.cartRepository
                )
            }
        }
    }
}