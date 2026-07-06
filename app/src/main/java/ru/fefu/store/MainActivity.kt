package ru.fefu.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.fefu.store.data.local.AssetCatalogDataSource
import ru.fefu.store.data.repository.AssetCatalogRepository
import ru.fefu.store.ui.theme.FEFUStoreTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val catalogRepository = AssetCatalogRepository(
            assetCatalogDataSource = AssetCatalogDataSource(
                context = applicationContext
            )
        )

        setContent {
            FEFUStoreTheme {
                StoreApp(
                    catalogRepository = catalogRepository
                )
            }
        }
    }
}