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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
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
    var isProcessing by remember { mutableStateOf(false) }

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
            isProcessing = false
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





    if (recognizedMusic == null) {

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
                            requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            if (isMicPermissionGranted) {
                                isProcessing = true
                                acrCloudManager.startRecognition()
                            }
                            else isProcessing = false
                        },
                        isProcessing
                    )
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
            }
        }
    } else {
        recognizedMusic?.let { music ->
            Column {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        Color.Transparent
                                    ),
                                    startY = 1000f,
                                    endY = 500f // Adjust the end position of the gradient as needed
                                )
                            )
                            .zIndex(1f)
                    )
                    // Add Image
                    if (albumArtUrl.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.soundwave),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(0f)
                        )
                    } else {
                        AsyncImage(
                            model = albumArtUrl,
                            contentDescription = "Cover Art",
                            placeholder = painterResource(id = R.drawable.soundwave),
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(0f)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .offset(y = -50.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        text = music.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 40.sp, // Adjust the font size as needed
                            lineHeight = 42.sp
                        )
                    )
                    Text(
                        text = "${music.artists.joinToString(", ") { it.name }} - ${music.album.name}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp, // Adjust the font size as needed
                            lineHeight = 26.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                recognizedMusic = null
                                isProcessing = false
                                albumArtUrl = null
                            }
                        ) {
                            Text(
                                text = "Restart",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
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