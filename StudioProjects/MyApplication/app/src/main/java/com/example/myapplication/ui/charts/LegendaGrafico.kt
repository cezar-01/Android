package com.example.myapplication.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.util.formatarMoeda
import com.example.myapplication.util.formatarPercentual

// Legenda de um grafico de pizza: uma linha por fatia, com cor, rotulo, valor e percentual.
@Composable
fun LegendaGrafico(
    fatias: List<FatiaGrafico>,
    modifier: Modifier = Modifier,
    mostrarPercentual: Boolean = true
) {
    val total = fatias.somaValores()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fatias.forEach { fatia ->
            val percentual = if (total == 0.0) 0f else (fatia.valor / total).toFloat()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quadradinho colorido representando a fatia
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(fatia.cor)
                )

                Text(
                    text = fatia.rotulo,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatarMoeda(fatia.valor),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (mostrarPercentual) {
                    Text(
                        text = formatarPercentual(percentual),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LegendaGraficoPreview() {
    LegendaGrafico(
        fatias = listOf(
            FatiaGrafico("Fixos", 1200.0, Color(0xFF2E7D32)),
            FatiaGrafico("Variaveis", 800.0, Color(0xFFEF6C00)),
            FatiaGrafico("Investimentos", 400.0, Color(0xFF6A1B9A))
        )
    )
}
