package com.ganjianping.lab.ak.features.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FirebaseFeatureScreen(
    analyticsStatus: String,
    crashlyticsStatus: String,
    remoteConfigStatus: String,
    performanceStatus: String,
    onBack: () -> Unit,
    onLogAnalytics: () -> Unit,
    onRecordCrashlytics: () -> Unit,
    onFetchRemoteConfig: () -> Unit,
    onRunPerformance: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                Text("‹  Dashboard")
            }
            Text("Firebase", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Run small, safe demonstrations of the Firebase services used by GJPLab.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            FirebaseActionCard(
                title = "Analytics",
                description = "Send a firebase_feature_opened event.",
                status = analyticsStatus,
                actionLabel = "Log event",
                onAction = onLogAnalytics
            )
            FirebaseActionCard(
                title = "Crashlytics",
                description = "Record a non-fatal demo exception without crashing the app.",
                status = crashlyticsStatus,
                actionLabel = "Record exception",
                onAction = onRecordCrashlytics
            )
            FirebaseActionCard(
                title = "Remote Config",
                description = "Fetch the maintenance-mode flag from Firebase.",
                status = remoteConfigStatus,
                actionLabel = "Fetch flag",
                onAction = onFetchRemoteConfig
            )
            FirebaseActionCard(
                title = "Performance Monitoring",
                description = "Run and complete a short custom performance trace.",
                status = performanceStatus,
                actionLabel = "Run trace",
                onAction = onRunPerformance
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FirebaseActionCard(
    title: String,
    description: String,
    status: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                description,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                status,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}
