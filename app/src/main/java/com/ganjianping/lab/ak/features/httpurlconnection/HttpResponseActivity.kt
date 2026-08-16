package com.ganjianping.lab.ak.features.httpurlconnection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ganjianping.lab.ak.common.theme.GJPLabTheme

class HttpResponseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GJPLabTheme {
                HttpResponseScreen(
                    statusCode = intent.getIntExtra(EXTRA_STATUS_CODE, 0),
                    body = intent.getStringExtra(EXTRA_BODY).orEmpty(),
                    headers = intent.getStringExtra(EXTRA_HEADERS).orEmpty(),
                    onBack = ::finish
                )
            }
        }
    }

    companion object {
        private const val EXTRA_STATUS_CODE = "status_code"
        private const val EXTRA_BODY = "body"
        private const val EXTRA_HEADERS = "headers"

        fun start(context: Context, response: HttpResponse) {
            context.startActivity(
                Intent(context, HttpResponseActivity::class.java).apply {
                    putExtra(EXTRA_STATUS_CODE, response.statusCode)
                    putExtra(EXTRA_BODY, response.body)
                    putExtra(EXTRA_HEADERS, response.headers.entries.joinToString("\n") { "${it.key}: ${it.value}" })
                }
            )
        }
    }
}
