
package com.ganjianping.ak.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class FeatureAction {
    DeviceInfo,
    HttpURLConnection
}

private data class Feature(
    val title: String,
    val description: String,
    val badge: String,
    val action: FeatureAction? = null
)

private val features = listOf(
    Feature("OS & hardware", "Explore device and Android details", "01", FeatureAction.DeviceInfo),
    Feature("HttpURLConnection", "Make requests with the native API", "02", FeatureAction.HttpURLConnection),
)

@Composable
fun MainScreen(onFeatureSelected: (FeatureAction) -> Unit) {
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(28.dp))
            Text("Feature lab", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Small, focused samples for exploring Android.", modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(feature, onClick = feature.action?.let { action -> { onFeatureSelected(action) } })
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, onClick: (() -> Unit)?) {
    val colors = if (feature.action != null) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
    }
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = colors
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(feature.badge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Column {
                Text(feature.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(feature.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (feature.action == null) {
                    Text("Coming soon", modifier = Modifier.padding(top = 10.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
