package ru.fefu.store.ui.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.fefu.store.domain.model.CartLineItem
import ru.fefu.store.ui.theme.StoreColors
import ru.fefu.store.util.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: CartUiState,
    onIncreaseClick: (CartLineItem) -> Unit,
    onDecreaseClick: (CartLineItem) -> Unit,
    onRemoveClick: (CartLineItem) -> Unit,
    onClearCartClick: () -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onCustomerEmailChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    onOrderSuccessDismiss: () -> Unit,
    onReturnHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isClearDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val successSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StoreColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CartHeader(
                isClearEnabled = uiState.items.isNotEmpty(),
                onClearClick = {
                    isClearDialogVisible = true
                }
            )

            if (uiState.items.isEmpty()) {
                EmptyCartState(
                    modifier = Modifier.weight(1f)
                )
            } else {
                CartContent(
                    modifier = Modifier.weight(1f),
                    uiState = uiState,
                    onIncreaseClick = onIncreaseClick,
                    onDecreaseClick = onDecreaseClick,
                    onRemoveClick = onRemoveClick,
                    onCustomerNameChange = onCustomerNameChange,
                    onCustomerEmailChange = onCustomerEmailChange,
                    onCommentChange = onCommentChange
                )

                CartBottomBlock(
                    totalPriceInKopecks = uiState.totalPriceInKopecks,
                    isCheckoutAvailable = uiState.isCheckoutAvailable,
                    onCheckoutClick = onCheckoutClick
                )
            }
        }
    }

    if (isClearDialogVisible) {
        ClearCartDialog(
            onConfirmClick = {
                isClearDialogVisible = false
                onClearCartClick()
            },
            onDismissRequest = {
                isClearDialogVisible = false
            }
        )
    }

    if (uiState.isOrderSuccessVisible) {
        OrderSuccessBottomSheet(
            sheetState = successSheetState,
            onDismissRequest = onOrderSuccessDismiss,
            onReturnHomeClick = onReturnHomeClick
        )
    }
}

@Composable
private fun CartHeader(
    isClearEnabled: Boolean,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Корзина",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = StoreColors.TextPrimary
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            enabled = isClearEnabled,
            onClick = onClearClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Очистить корзину",
                tint = if (isClearEnabled) {
                    StoreColors.TextSecondary
                } else {
                    StoreColors.TextSecondary.copy(alpha = 0.35f)
                }
            )
        }
    }
}

@Composable
private fun CartContent(
    uiState: CartUiState,
    onIncreaseClick: (CartLineItem) -> Unit,
    onDecreaseClick: (CartLineItem) -> Unit,
    onRemoveClick: (CartLineItem) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onCustomerEmailChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            items = uiState.items,
            key = { item -> "${item.product.id}_${item.size.id}" }
        ) { item ->
            CartItemRow(
                item = item,
                onIncreaseClick = {
                    onIncreaseClick(item)
                },
                onDecreaseClick = {
                    onDecreaseClick(item)
                },
                onRemoveClick = {
                    onRemoveClick(item)
                }
            )
        }

        item {
            CheckoutForm(
                name = uiState.customerName,
                email = uiState.customerEmail,
                comment = uiState.comment,
                onNameChange = onCustomerNameChange,
                onEmailChange = onCustomerEmailChange,
                onCommentChange = onCommentChange
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartLineItem,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 12.dp, top = 12.dp, end = 6.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(width = 70.dp, height = 86.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(StoreColors.ChipBackground),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoreColors.TextPrimary
                )

                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "${item.product.shortDescription}\n${item.size.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = StoreColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatPrice(item.totalPriceInKopecks),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoreColors.Accent
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onRemoveClick()
                        },
                    text = "×",
                    style = MaterialTheme.typography.titleMedium,
                    color = StoreColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(30.dp))

                QuantityControl(
                    quantity = item.quantity,
                    onDecreaseClick = onDecreaseClick,
                    onIncreaseClick = onIncreaseClick
                )
            }
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = StoreColors.Divider
    )
}

@Composable
private fun QuantityControl(
    quantity: Int,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(StoreColors.ChipBackground),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 32.dp)
                .clickable {
                    onDecreaseClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary
            )
        }

        Text(
            modifier = Modifier.width(28.dp),
            text = quantity.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = StoreColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 32.dp)
                .clickable {
                    onIncreaseClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary
            )
        }
    }
}

@Composable
private fun CheckoutForm(
    name: String,
    email: String,
    comment: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCommentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StoreColors.Background)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CartTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Имя*",
            singleLine = true
        )

        CartTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Почта*",
            singleLine = true
        )

        CartTextField(
            modifier = Modifier.height(96.dp),
            value = comment,
            onValueChange = onCommentChange,
            placeholder = "Комментарий к заказу",
            singleLine = false
        )
    }
}

@Composable
private fun CartTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodySmall,
                color = StoreColors.TextSecondary
            )
        },
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = StoreColors.Accent
        )
    )
}

@Composable
private fun CartBottomBlock(
    totalPriceInKopecks: Long,
    isCheckoutAvailable: Boolean,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Итого",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formatPrice(totalPriceInKopecks),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(48.dp),
            enabled = isCheckoutAvailable,
            onClick = onCheckoutClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StoreColors.Accent,
                contentColor = Color.White,
                disabledContainerColor = StoreColors.Accent.copy(alpha = 0.45f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Оформить",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyCartState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Корзина пуста",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = StoreColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Добавьте товары из каталога, чтобы оформить заказ.",
            style = MaterialTheme.typography.bodyMedium,
            color = StoreColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ClearCartDialog(
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Очистить корзину?")
        },
        text = {
            Text(text = "Все товары будут удалены из корзины.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text(text = "Очистить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = "Отмена")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderSuccessBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onReturnHomeClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 14.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .border(
                        border = BorderStroke(
                            width = 3.dp,
                            color = StoreColors.TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    tint = StoreColors.TextSecondary
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                text = "Заказ успешно оформлен",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                text = "Подтверждение и чек отправили на вашу почту",
                style = MaterialTheme.typography.bodySmall,
                color = StoreColors.TextSecondary,
                textAlign = TextAlign.Start
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(48.dp),
                onClick = onReturnHomeClick,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StoreColors.Accent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Вернуться на главную",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}