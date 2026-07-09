package ru.fefu.store.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.fefu.store.data.repository.CartRepository
import ru.fefu.store.domain.checkout.CheckoutValidator
import ru.fefu.store.domain.model.CartLineItem
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
        observeTotalQuantity()
    }

    fun addToCart(product: Product, selectedSize: ProductSize?) {
        if (selectedSize == null) {
            return
        }

        viewModelScope.launch {
            cartRepository.addToCart(
                productId = product.id,
                sizeId = selectedSize.id,
            )
        }
    }

    fun increaseQuantity(item: CartLineItem) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(
                productId = item.product.id,
                sizeId = item.size.id,
            )
        }
    }

    fun decreaseQuantity(item: CartLineItem) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(
                productId = item.product.id,
                sizeId = item.size.id,
            )
        }
    }

    fun removeItem(item: CartLineItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(
                productId = item.product.id,
                sizeId = item.size.id,
            )
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun onCustomerNameChange(value: String) {
        _uiState.update { state ->
            state.copy(
                customerName = value,
            ).withValidation()
        }
    }

    fun onCustomerEmailChange(value: String) {
        _uiState.update { state ->
            state.copy(
                customerEmail = value,
            ).withValidation()
        }
    }

    fun onCommentChange(value: String) {
        _uiState.update { state ->
            state.copy(
                comment = value,
            )
        }
    }

    fun checkout() {
        val state = _uiState.value.withValidation()

        if (!state.isCheckoutAvailable) {
            _uiState.value = state
            return
        }

        viewModelScope.launch {
            cartRepository.clearCart()

            _uiState.update { currentState ->
                currentState.copy(
                    customerName = "",
                    customerEmail = "",
                    comment = "",
                    isOrderSuccessVisible = true,
                ).withValidation()
            }
        }
    }

    fun dismissOrderSuccess() {
        _uiState.update { state ->
            state.copy(
                isOrderSuccessVisible = false,
            )
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.observeCart().collect { cart ->
                _uiState.update { state ->
                    state.copy(
                        items = cart.items,
                        totalPriceInKopecks = cart.totalPriceInKopecks,
                    ).withValidation()
                }
            }
        }
    }

    private fun observeTotalQuantity() {
        viewModelScope.launch {
            cartRepository.observeTotalQuantity().collect { totalQuantity ->
                _uiState.update { state ->
                    state.copy(
                        totalQuantity = totalQuantity,
                    )
                }
            }
        }
    }

    private fun CartUiState.withValidation(): CartUiState {
        return copy(
            isCheckoutAvailable = CheckoutValidator.isCheckoutAvailable(
                name = customerName,
                email = customerEmail,
                hasItems = items.isNotEmpty()
            )
        )
    }

    class Factory(private val cartRepository: CartRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CartViewModel(
            cartRepository = cartRepository,
        ) as T
    }
}
