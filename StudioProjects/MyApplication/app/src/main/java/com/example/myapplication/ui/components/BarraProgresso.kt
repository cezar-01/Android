package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarraProgresso(
    progresso: Float,
    modifier: Modifier = Modifier,
    cor: Color = MaterialTheme.colorScheme.primary,
    altura: Dp = 10.dp
) {
    val alvo = progresso.coerceIn(0f, 1f)
    val animado by animateFloatAsState(targetValue = alvo, label = "progresso")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(altura)
            .clip(RoundedCornerShape(50))
            .background(cor.copy(alpha = 0.18f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animado)
                .fillMaxHeight()
                .background(cor, RoundedCornerShape(50))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BarraProgressoPreview() {
    BarraProgresso(progresso = 0.65f)
}
