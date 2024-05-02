package com.reisdis.musicrecognizer

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent

@Composable
fun CircularWaveButton(
    onClick: () -> Unit
) {


    var isProcessing by remember { mutableStateOf(false) }


    val transition = updateTransition(targetState = isProcessing, label = "MicButtonTransition")
    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Scale"
    ) { state ->
        if (state) 1.2f else 1f
    }

    val color by transition.animateColor(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Color"
    ) { state ->
        if (state) Color.Red else Color.Blue
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row {
            Spacer(Modifier.weight(1f))
            Column {
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        isProcessing = !isProcessing
                        onClick()

                    },
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scale)
                        .graphicsLayer {
                            clip = true
                            shape = CircleShape
                        }
                        .background(
                            color = color,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector =  ImageVector.vectorResource(id = R.drawable.mic),
                        contentDescription = "Mic Icon",
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

