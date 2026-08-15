package com.ganjianping.ak.sample.features.httpurlconnection

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HttpURLConnectionScreen(
    repository: HttpURLConnectionRepository,
    onBack: () -> Unit,
    onResponse: (HttpResponse) -> Unit,
    onError: (String) -> Unit
) {
    var method by remember { mutableStateOf(HttpMethod.GET) }
    var url by remember {
        mutableStateOf("https://www.ganjianping.com/api/open/websites?channel=AI&page=0&size=500&lang=EN")
    }
    var payload by remember { mutableStateOf("{\n  \"example\": \"value\"\n}") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("‹  Main") }
            Text("HttpURLConnection", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Build and send an HTTP request with the native Android API.",
                modifier = Modifier.padding(top = 6.dp, bottom = 22.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HttpMethod.entries.forEach { option ->
                    FilterChip(
                        selected = method == option,
                        onClick = { method = option; errorMessage = null },
                        label = { Text(option.name) }
                    )
                }
            }

            OutlinedTextField(
                value = url,
                onValueChange = { url = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                label = { Text("URL") },
                placeholder = { Text("https://example.com/api") },
                singleLine = false
            )

            if (method.supportsPayload) {
                OutlinedTextField(
                    value = payload,
                    onValueChange = { payload = it; errorMessage = null },
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    label = { Text("Request payload") },
                    placeholder = { Text("JSON payload") },
                    minLines = 6
                )
            }

            errorMessage?.let { message ->
                Text(
                    message,
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            onResponse(repository.execute(method, url, payload))
                        } catch (exception: Exception) {
                            val message = exception.message ?: "Request failed"
                            errorMessage = message
                            onError(message)
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && url.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 22.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Send request")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun HttpErrorBanner(message: String, onDismiss: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    }
}
