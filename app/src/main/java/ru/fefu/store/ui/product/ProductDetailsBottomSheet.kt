package ru.fefu.store.ui.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.fefu.store.R
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize
import ru.fefu.store.ui.theme.StoreColors
import ru.fefu.store.util.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsBottomSheet(
    product: Product,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onAddToCartClick: (product: Product, selectedSize: ProductSize?) -> Unit,
) {
    var selectedSizeId by rememberSaveable(product.id) {
        mutableStateOf(product.sizes.firstOrNull()?.id.orEmpty())
    }

    var isInfoDialogVisible by rememberSaveable(product.id) {
        mutableStateOf(false)
    }

    val selectedSize = product.sizes.firstOrNull { size ->
        size.id == selectedSizeId
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.72f

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = StoreColors.SheetBackground,
        shape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
        ),
        dragHandle = null,
    ) {
        ProductDetailsContent(
            sheetHeight = sheetHeight,
            product = product,
            selectedSizeId = selectedSizeId,
            onSizeClick = { size ->
                selectedSizeId = size.id
            },
            onInfoClick = {
                isInfoDialogVisible = true
            },
            onAddToCartClick = {
                onAddToCartClick(product, selectedSize)
            },
        )
    }

    if (isInfoDialogVisible) {
        ProductInfoDialog(
            product = product,
            onDismissRequest = {
                isInfoDialogVisible = false
            },
        )
    }
}

@Composable
private fun ProductDetailsContent(
    sheetHeight: androidx.compose.ui.unit.Dp,
    product: Product,
    selectedSizeId: String,
    onSizeClick: (ProductSize) -> Unit,
    onInfoClick: () -> Unit,
    onAddToCartClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .navigationBarsPadding()
            .background(StoreColors.SheetBackground),
    ) {
        ProductImageBlock(product = product)

        ProductDescriptionBlock(
            product = product,
            onInfoClick = onInfoClick,
        )

        Spacer(modifier = Modifier.weight(1f))

        ProductBottomControls(
            product = product,
            selectedSizeId = selectedSizeId,
            onSizeClick = onSizeClick,
            onAddToCartClick = onAddToCartClick,
        )
    }
}

@Composable
private fun ProductImageBlock(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(StoreColors.SheetBackground),
    ) {
        ProductTags(
            tags = product.tags,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 12.dp),
        )

        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.72f)
                .height(120.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ProductDescriptionBlock(product: Product, onInfoClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.width(8.dp))

            ProductInfoButton(
                onClick = onInfoClick,
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = product.longDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = StoreColors.TextSecondary,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProductInfoButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(StoreColors.AccentLight)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "i",
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = StoreColors.Accent,
        )
    }
}

@Composable
private fun ProductTags(tags: List<String>, modifier: Modifier = Modifier) {
    if (tags.isEmpty()) {
        return
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(
            items = tags,
            key = { tag -> tag },
        ) { tag ->
            ProductTagChip(tag = tag)
        }
    }
}

@Composable
private fun ProductTagChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(StoreColors.Accent)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = tag.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@Composable
private fun ProductBottomControls(
    product: Product,
    selectedSizeId: String,
    onSizeClick: (ProductSize) -> Unit,
    onAddToCartClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StoreColors.SheetBackground)
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp, bottom = 12.dp),
    ) {
        SizeSelector(
            sizes = product.sizes,
            selectedSizeId = selectedSizeId,
            onSizeClick = onSizeClick,
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(50.dp),
            onClick = onAddToCartClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StoreColors.Accent,
                contentColor = Color.White,
            ),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.product_details_add_to_cart_with_price,
                    formatPrice(product.priceInKopecks),
                ),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SizeSelector(sizes: List<ProductSize>, selectedSizeId: String, onSizeClick: (ProductSize) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = sizes,
            key = { size -> size.id },
        ) { size ->
            val selected = size.id == selectedSizeId

            FilterChip(
                modifier = Modifier.defaultMinSize(
                    minWidth = 42.dp,
                    minHeight = 34.dp,
                ),
                selected = selected,
                onClick = {
                    onSizeClick(size)
                },
                label = {
                    Text(
                        text = size.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
                shape = RoundedCornerShape(50),
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = StoreColors.ChipBackground,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = StoreColors.ChipBackground,
                    labelColor = StoreColors.TextPrimary,
                    selectedContainerColor = StoreColors.Accent,
                    selectedLabelColor = Color.White,
                ),
            )
        }
    }
}
