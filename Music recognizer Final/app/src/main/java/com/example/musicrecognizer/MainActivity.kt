package com.example.musicrecognizer

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicrecognizer.ui.theme.MusicRecognizerTheme
import java.io.File
import java.io.IOException

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
                    AudioLayout()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
@Composable
fun AudioLayout() {
    val context = LocalContext.current
    var isMicPermissionGranted by remember { mutableStateOf(false) }
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isMicPermissionGranted = isGranted
        }

    var recordBool by remember { mutableStateOf(false) }
    var audioFile: File? by remember { mutableStateOf(null) }

    LaunchedEffect(recordBool) {
        if (isMicPermissionGranted) {
            val audioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build()

            val minBufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val audioRecord = AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(minBufferSize)
                .build()

            val audioData = ByteArray(minBufferSize)

            val outputFile = File(context.cacheDir, "temp_audio.pcm")
            val outputStream = outputFile.outputStream()

            audioRecord.startRecording()
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 10_000) {
                val bytesRead = audioRecord.read(audioData, 0, minBufferSize)
                outputStream.write(audioData, 0, bytesRead)
            }

            audioRecord.stop()
            audioRecord.release()
            outputStream.close()

            // Set the recorded audio file
            audioFile = outputFile
            recordBool = false
        }
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
            onClick = {
                if (isMicPermissionGranted) {
                    // Start recording
                    recordBool = true
                }
            }
        ) {
            Text(text = "Record Audio for 10 Seconds")
        }
        Text(text = "Is Recording: $recordBool")

        audioFile?.let { audioFile ->
            Button(
                onClick = {
                    val mediaPlayer = MediaPlayer().apply {
                        try {
                            reset()
                            setDataSource(audioFile.path)
                            prepare()
                            start()
                        } catch (e: IOException) {
                            // Handle IOException (Prepare failed)
                            Log.e(TAG, "IOException: Prepare failed", e)
                            Toast.makeText(context, "Failed to prepare media player", Toast.LENGTH_SHORT).show()
                        } catch (e: IllegalArgumentException) {
                            // Handle IllegalArgumentException
                            Log.e(TAG, "IllegalArgumentException: Invalid audio file", e)
                            Toast.makeText(context, "Invalid audio file", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text(text = "Play Recorded Audio")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicRecognizerTheme {
        AudioLayout()
    }
}