package com.example.careconnect.screens.generatecode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import qrgenerator.qrkitpainter.rememberQrKitPainter

@Composable
fun GenerateQRCode(
    inputText: String
){
    val painter = rememberQrKitPainter(data = inputText)

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(100.dp)
    )
}
