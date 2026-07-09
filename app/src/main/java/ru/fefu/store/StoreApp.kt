package ru.fefu.store

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.fefu.store.data.repository.CartRepository
import ru.fefu.store.data.repository.CatalogRepository
import ru.fefu.store.domain.model.Product
import ru.fefu.store.ui.cart.CartScreen
import ru.fefu.store.ui.cart.CartViewModel
import ru.fefu.store.ui.catalog.CatalogScreen
import ru.fefu.store.ui.catalog.CatalogViewModel
import ru.fefu.store.ui.product.ProductDetailsBottomSheet
import ru.fefu.store.ui.theme.StoreColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreApp(catalogRepository: CatalogRepository, cartRepository: CartRepository) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(StoreDestination.Catalog.route)
    }

    var selectedProduct by remember {
        mutableStateOf<Product?>(null)
    }

    val productSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.Factory(catalogRepository),
    )

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModel.Factory(cartRepository),
    )

    val catalogUiState by catalogViewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            StoreBottomNavigationBar(
                selectedRoute = selectedDestination,
                cartItemsCount = cartUiState.totalQuantity,
                onDestinationClick = { route ->
                    selectedDestination = route
                },
            )
        },
    ) { innerPadding ->
        when (selectedDestination) {
            StoreDestination.Catalog.route -> {
                CatalogScreen(
                    modifier = Modifier.padding(innerPadding),
                    uiState = catalogUiState,
                    onCategoryClick = catalogViewModel::selectCategory,
                    onProductClick = { product ->
                        selectedProduct = product
                    },
                    onRetryClick = catalogViewModel::refreshCatalog,
                )
            }

            StoreDestination.Cart.route -> {
                CartScreen(
                    modifier = Modifier.padding(innerPadding),
                    uiState = cartUiState,
                    onIncreaseClick = cartViewModel::increaseQuantity,
                    onDecreaseClick = cartViewModel::decreaseQuantity,
                    onRemoveClick = cartViewModel::removeItem,
                    onClearCartClick = cartViewModel::clearCart,
                    onCustomerNameChange = cartViewModel::onCustomerNameChange,
                    onCustomerEmailChange = cartViewModel::onCustomerEmailChange,
                    onCommentChange = cartViewModel::onCommentChange,
                    onCheckoutClick = cartViewModel::checkout,
                    onOrderSuccessDismiss = cartViewModel::dismissOrderSuccess,
                    onReturnHomeClick = {
                        cartViewModel.dismissOrderSuccess()
                        selectedDestination = StoreDestination.Catalog.route
                    },
                )
            }
        }
    }

    selectedProduct?.let { product ->
        ProductDetailsBottomSheet(
            product = product,
            sheetState = productSheetState,
            onDismissRequest = {
                selectedProduct = null
            },
            onAddToCartClick = { addedProduct, selectedSize ->
                cartViewModel.addToCart(
                    product = addedProduct,
                    selectedSize = selectedSize,
                )
                selectedProduct = null
            },
        )
    }
}

@Composable
private fun StoreBottomNavigationBar(selectedRoute: String, cartItemsCount: Int, onDestinationClick: (String) -> Unit) {
    val navigationItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = StoreColors.Accent,
        selectedTextColor = StoreColors.Accent,
        indicatorColor = StoreColors.AccentLight,
    )

    val catalogLabel = stringResource(R.string.nav_catalog)
    val cartLabel = stringResource(R.string.nav_cart)

    NavigationBar(
        containerColor = StoreColors.BottomNavigationBackground,
    ) {
        NavigationBarItem(
            selected = selectedRoute == StoreDestination.Catalog.route,
            onClick = {
                onDestinationClick(StoreDestination.Catalog.route)
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.List,
                    contentDescription = catalogLabel,
                )
            },
            label = {
                Text(text = catalogLabel)
            },
            colors = navigationItemColors,
        )

        NavigationBarItem(
            selected = selectedRoute == StoreDestination.Cart.route,
            onClick = {
                onDestinationClick(StoreDestination.Cart.route)
            },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemsCount > 0) {
                            Badge(
                                containerColor = StoreColors.Accent,
                                contentColor = androidx.compose.ui.graphics.Color.White,
                            ) {
                                Text(
                                    text = if (cartItemsCount > 99) {
                                        "99+"
                                    } else {
                                        cartItemsCount.toString()
                                    },
                                )
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = cartLabel,
                    )
                }
            },
            label = {
                Text(text = cartLabel)
            },
            colors = navigationItemColors,
        )
    }
}

private enum class StoreDestination(val route: String) {
    Catalog(route = "catalog"),
    Cart(route = "cart"),
}
