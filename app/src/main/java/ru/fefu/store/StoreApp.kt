package ru.fefu.store

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.fefu.store.data.repository.CatalogRepository
import ru.fefu.store.ui.cart.CartPlaceholderScreen
import ru.fefu.store.ui.catalog.CatalogScreen
import ru.fefu.store.ui.catalog.CatalogViewModel
import ru.fefu.store.ui.catalog.StoreColors

@Composable
fun StoreApp(
    catalogRepository: CatalogRepository
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(StoreDestination.Catalog.route)
    }

    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.Factory(catalogRepository)
    )

    val catalogUiState by catalogViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            StoreBottomNavigationBar(
                selectedRoute = selectedDestination,
                onDestinationClick = { route ->
                    selectedDestination = route
                }
            )
        }
    ) { innerPadding ->
        when (selectedDestination) {
            StoreDestination.Catalog.route -> {
                CatalogScreen(
                    modifier = Modifier.padding(innerPadding),
                    uiState = catalogUiState,
                    onCategoryClick = catalogViewModel::selectCategory,
                    onRetryClick = catalogViewModel::loadCatalog
                )
            }

            StoreDestination.Cart.route -> {
                CartPlaceholderScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun StoreBottomNavigationBar(
    selectedRoute: String,
    onDestinationClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color.White
    ) {
        NavigationBarItem(
            selected = selectedRoute == StoreDestination.Catalog.route,
            onClick = {
                onDestinationClick(StoreDestination.Catalog.route)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Меню")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StoreColors.Accent,
                selectedTextColor = StoreColors.Accent,
                indicatorColor = StoreColors.AccentLight
            )
        )

        NavigationBarItem(
            selected = selectedRoute == StoreDestination.Cart.route,
            onClick = {
                onDestinationClick(StoreDestination.Cart.route)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Корзина")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StoreColors.Accent,
                selectedTextColor = StoreColors.Accent,
                indicatorColor = StoreColors.AccentLight
            )
        )
    }
}

private enum class StoreDestination(
    val route: String
) {
    Catalog(route = "catalog"),
    Cart(route = "cart")
}