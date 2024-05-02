package com.reisdis.musicrecognizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.reisdis.musicrecognizer.ui.theme.MusicRecognizerTheme

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
    var recognizedMusic by remember { mutableStateOf<Music?>(null) }

    // Create a launcher for the permission request
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isMicPermissionGranted = isGranted
        }

    val acrCloudManager = remember {
        ACRCloudManager(context) { result ->
            recognizedMusic = result.metadata.musics[0]
        }
    }

    LaunchedEffect(Unit) {
        // Initialize ACRCloudManager when composable is launched
        acrCloudManager.initAcrcloud()
    }

    var albumArtUrl: String? by remember { mutableStateOf(null) }

    LaunchedEffect(recognizedMusic) {
        recognizedMusic?.let {
            it.externalMetadata.spotify?.track?.id?.let {
                getTrackInfo(it) {
                    print(it)
                    albumArtUrl = it
                }
            }
        }
    }





    if (recognizedMusic != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Spacer(Modifier.weight(1f))
                Column {
                    Spacer(Modifier.weight(1f))
                    CircularWaveButton(
                        onClick = {
                            acrCloudManager.startRecognition()
                        }
                    )
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
            }
        }
    } else {
        recognizedMusic?.let { music ->
            AsyncImage(
                model = albumArtUrl,
                contentDescription = "Cover Art",
                placeholder = painterResource(id = R.drawable.soundwave)
            )

            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(8.dp)
            ) {

                Text("Recognition Result:", fontWeight = FontWeight.Bold)

                Text("Title: ${music.title}")
                Text("Album: ${music.album.name}")
                Text("Genres: ${music.genres?.joinToString(", ") { it.name }}")
                Text("Artists:")
                music.artists.forEach { artist ->
                    Text("  - ${artist.name}")
                }
                Text("External Metadata:")



            }
        }
        Column {
            Button(
                onClick = {
                    requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }
            ) {
                Text(text = "Request Mic Permission")
            }

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

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    MusicRecognizerTheme {
        RecordAudioScreen()
    }
}