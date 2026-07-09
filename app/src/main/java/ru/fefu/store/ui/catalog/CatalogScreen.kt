package ru.fefu.store.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product
import ru.fefu.store.ui.theme.StoreColors
import androidx.compose.ui.res.stringResource
import ru.fefu.store.R

@Composable
fun CatalogScreen(
    uiState: CatalogUiState,
    onCategoryClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StoreColors.Background)
    ) {
        when (uiState) {
            CatalogUiState.Loading -> {
                LoadingState()
            }

            CatalogUiState.Error -> {
                ErrorState(
                    onRetryClick = onRetryClick
                )
            }

            is CatalogUiState.Content -> {
                CatalogContent(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    products = uiState.products,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}

@Composable
private fun CatalogContent(
    categories: List<Category>,
    selectedCategoryId: String,
    products: List<Product>,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CategoryTabs(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategoryClick = onCategoryClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(
                items = products,
                key = { product -> product.id }
            ) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = categories,
            key = { category -> category.id }
        ) { category ->
            val selected = category.id == selectedCategoryId

            FilterChip(
                selected = selected,
                onClick = {
                    onCategoryClick(category.id)
                },
                label = {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = RoundedCornerShape(50),
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = StoreColors.ChipBackground,
                    labelColor = StoreColors.TextPrimary,
                    selectedContainerColor = StoreColors.Accent,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = StoreColors.Accent
        )
    }
}

@Composable
private fun ErrorState(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.catalog_error_message),
            style = MaterialTheme.typography.titleMedium,
            color = StoreColors.TextPrimary
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.catalog_error_description),
            style = MaterialTheme.typography.bodyMedium,
            color = StoreColors.TextSecondary
        )

        Button(
            modifier = Modifier.padding(top = 20.dp),
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = StoreColors.Accent
            )
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}
