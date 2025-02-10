package com.example.realtimechatbot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun ChatBotApp(modifier: Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }
    var responseText by remember { mutableStateOf("Press the button to start") }
    val textToSpeech = remember { TextToSpeech(context) { } }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(responseText, modifier = Modifier.padding(16.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    isListening = !isListening
                    if (isListening) {
                        responseText = "Listening..."
                        scope.launch {
                            val response = chatWithOpenAI("Hello!")
                            responseText = response
                            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    } else {
                        textToSpeech.stop()
                    }
                }) {
                    Text(if (isListening) "Stop" else "Start")
                }
            }
        }
    )
}

suspend fun chatWithOpenAI(prompt: String): String {
    // This is where the API call to OpenAI's Realtime API would be made.
    // Replace this with actual API call logic.
    return "Hello, this is OpenAI! How can I help you?"
}
