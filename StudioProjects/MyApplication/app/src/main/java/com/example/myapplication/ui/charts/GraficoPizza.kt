package com.example.myapplication.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.CalculadoraGastosTheme

private val CorAnelVazio = Color(0xFFE0E0E0)

@Composable
fun GraficoPizza(
    fatias: List<FatiaGrafico>,
    modifier: Modifier = Modifier,
    tamanho: Dp = 180.dp,
    espessura: Dp = 34.dp,
    textoCentral: String? = null,
    rotuloCentral: String? = null
) {
    val fatiasValidas = fatias.filter { it.valor > 0 }
    val total = fatiasValidas.sumOf { it.valor }

    var progressoAlvo by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(fatias) {
        progressoAlvo = 0f
        progressoAlvo = 1f
    }
    val animado by animateFloatAsState(targetValue = progressoAlvo, animationSpec = tween(700), label = "pizza")

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(tamanho)) {
            val larguraTraco = espessura.toPx()
            val topLeft = Offset(larguraTraco / 2f, larguraTraco / 2f)
            val tamanhoArco = Size(size.width - larguraTraco, size.height - larguraTraco)

            if (total <= 0.0 || fatiasValidas.isEmpty()) {
                drawArc(
                    color = CorAnelVazio,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = larguraTraco, cap = StrokeCap.Butt),
                    topLeft = topLeft,
                    size = tamanhoArco
                )
            } else {
                val respiro = if (fatiasValidas.size > 1) 1.5f else 0f
                var anguloInicial = -90f
                for (fatia in fatiasValidas) {
                    val fracao = (fatia.valor / total).toFloat()
                    val sweepBase = (fracao * 360f - respiro).coerceAtLeast(0f)
                    val sweepAnimado = sweepBase * animado
                    drawArc(
                        color = fatia.cor,
                        startAngle = anguloInicial,
                        sweepAngle = sweepAnimado,
                        useCenter = false,
                        style = Stroke(width = larguraTraco, cap = StrokeCap.Butt),
                        topLeft = topLeft,
                        size = tamanhoArco
                    )
                    anguloInicial += fracao * 360f
                }
            }
        }

        if (textoCentral != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = textoCentral,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                if (rotuloCentral != null) {
                    Text(
                        text = rotuloCentral,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GraficoPizzaPreview() {
    CalculadoraGastosTheme {
        GraficoPizza(
            fatias = listOf(
                FatiaGrafico("Fixos", 1200.0, Color(0xFF3F51B5)),
                FatiaGrafico("Variaveis", 800.0, Color(0xFFFF9800)),
                FatiaGrafico("Investimentos", 400.0, Color(0xFF9C27B0))
            ),
            textoCentral = "R$ 2.400,00",
            rotuloCentral = "Total"
        )
    }
}
