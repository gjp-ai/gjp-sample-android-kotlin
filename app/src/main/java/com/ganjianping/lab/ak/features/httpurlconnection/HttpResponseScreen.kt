package com.ganjianping.lab.ak.features.httpurlconnection

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HttpResponseScreen(
    statusCode: Int,
    body: String,
    headers: String,
    onBack: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("‹  Request") }
            Text("Response", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "HttpURLConnection response details",
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("HTTP status", fontWeight = FontWeight.Bold)
                    Text(statusCode.toString(), modifier = Modifier.padding(top = 6.dp), color = statusColor(statusCode))
                }
            }
            ResponseBlock("Response JSON", body)
            if (headers.isNotBlank()) {
                ResponseBlock("Headers", headers)
            }
            Spacer(Modifier.height(18.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back to request") }
        }
    }
}

@Composable
private fun ResponseBlock(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
        Column(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(18.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            SelectionContainer {
                Text(value.ifBlank { "(empty response)" }, modifier = Modifier.padding(top = 10.dp), fontFamily = FontFamily.Monospace)
            }
        }
    }
}

private fun statusColor(statusCode: Int) = when {
    statusCode in 200..299 -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
    statusCode in 400..599 -> androidx.compose.ui.graphics.Color(0xFFC62828)
    else -> androidx.compose.ui.graphics.Color.Unspecified
}
