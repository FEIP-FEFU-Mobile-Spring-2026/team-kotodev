package ru.fefu.store.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.fefu.store.domain.model.Product
import ru.fefu.store.ui.theme.StoreColors
import ru.fefu.store.util.formatPrice

@Composable
fun ProductCard(
    product: Product,
    onClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(StoreColors.Background)
            .clickable {
                onClick(product)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        ProductImage(
            imageUrl = product.imageUrl,
            productName = product.name
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StoreColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = product.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = StoreColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onClick(product)
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StoreColors.AccentLight,
                    contentColor = StoreColors.Accent
                )
            ) {
                Text(
                    text = formatPrice(product.priceInKopecks),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductImage(
    imageUrl: String,
    productName: String
) {
    Box(
        modifier = Modifier
            .size(width = 112.dp, height = 136.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(StoreColors.ChipBackground),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = productName,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.82f),
            contentScale = ContentScale.Crop
        )
    }
}