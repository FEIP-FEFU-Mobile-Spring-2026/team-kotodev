package ru.fefu.store.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.fefu.store.R
import ru.fefu.store.domain.model.Product
import ru.fefu.store.ui.theme.StoreColors

@Composable
fun ProductInfoDialog(
    product: Product,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.product_info_title),
                color = StoreColors.TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductInfoRow(
                    label = stringResource(R.string.product_info_material),
                    value = product.material
                )

                ProductInfoRow(
                    label = stringResource(R.string.product_info_weight),
                    value = product.weight
                )

                ProductInfoRow(
                    label = stringResource(R.string.product_info_season),
                    value = product.season
                )

                ProductInfoRow(
                    label = stringResource(R.string.product_info_country),
                    value = product.countryOfOrigin
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.product_info_close))
            }
        }
    )
}

@Composable
private fun ProductInfoRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = StoreColors.TextSecondary
        )

        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = StoreColors.TextPrimary
        )
    }
}