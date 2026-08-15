package com.ganjianping.ak.sample.features.deviceinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ganjianping.ak.sample.features.deviceinfo.data.DeviceInfoRepository

@Composable
fun DeviceInfoScreen(repository: DeviceInfoRepository, onBack: () -> Unit) {
    val info = remember(repository) { repository.read() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("‹  Dashboard") }
            Text("OS & hardware", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "A snapshot of the device this app is running on.",
                modifier = Modifier.padding(top = 6.dp, bottom = 22.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InfoSection("Android OS", info.take(4))
            Spacer(Modifier.height(18.dp))
            InfoSection("Hardware", info.drop(4))
            Spacer(Modifier.height(18.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back to dashboard") }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoSection(title: String, entries: List<Pair<String, String>>) {
    Card {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
            Text(title, modifier = Modifier.padding(vertical = 10.dp), fontWeight = FontWeight.Bold)
            entries.forEachIndexed { index, (label, value) ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, modifier = Modifier.padding(start = 16.dp), textAlign = TextAlign.End, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
