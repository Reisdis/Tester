package com.reisdis.musicrecognizer

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reisdis.musicrecognizer.ui.theme.MusicRecognizerTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicRecognizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecordAudioScreen()
                }
            }
        }
    }
}

@Composable
fun RecordAudioScreen() {
    // Context needed for permission request
    val context = LocalContext.current

    // State to hold the result of the permission request
    var isMicPermissionGranted by remember { mutableStateOf(false) }

    // State to hold the recognition result
    var recognitionResult by remember { mutableStateOf<RecognitionResult?>(null) }

    // Create a launcher for the permission request
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isMicPermissionGranted = isGranted
        }

    val acrCloudManager = remember {
        ACRCloudManager(context) { result ->
            // Update recognition result state
            recognitionResult = result
        }
    }

    LaunchedEffect(Unit) {
        // Initialize ACRCloudManager when composable is launched
        acrCloudManager.initAcrcloud()
    }

    Column {
        Button(
            onClick = {
                // Request microphone permission
                requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        ) {
            Text(text = "Request Mic Permission")
        }

        // Display the result of the permission request
        Text(
            text = "Microphone Permission Granted: $isMicPermissionGranted",
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = { if (isMicPermissionGranted) acrCloudManager.startRecognition() },
            enabled = isMicPermissionGranted
        ) {
            Text(text = "Recognize")
        }

        // Display recognition result if available
        recognitionResult?.let { result ->
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text("Recognition Result:", fontWeight = FontWeight.Bold)
                Text("Cost Time: ${result.costTime}")
                Text("Status: ${result.status.msg}")

                // Display all songs, albums, and artists
                result.metadata.humming.forEachIndexed { index, humming ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Song ${index + 1}:", fontWeight = FontWeight.Bold)
                        Text("Title: ${humming.title}")
                        Text("Album: ${humming.album.name}")
                        Text("Artists:")
                        humming.artists.forEach { artist ->
                            Text("  - ${artist.name}")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicRecognizerTheme {
        RecordAudioScreen()
    }
}