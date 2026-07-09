package ru.fefu.store.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.fefu.store.ui.theme.StoreColors

@Composable
fun CartPlaceholderScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StoreColors.Background)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Корзина",
            style = MaterialTheme.typography.titleLarge,
            color = StoreColors.TextPrimary
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Корзина будет реализована в следующих блоках.",
            style = MaterialTheme.typography.bodyMedium,
            color = StoreColors.TextSecondary
        )
    }
}