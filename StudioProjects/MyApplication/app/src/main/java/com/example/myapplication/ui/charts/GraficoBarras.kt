package com.example.myapplication.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.util.formatarMoeda

@Composable
fun GraficoBarras(
    barras: List<BarraGrafico>,
    modifier: Modifier = Modifier,
    alturaMaxima: Dp = 160.dp
) {
    if (barras.isEmpty()) {
        Text(
            text = "Sem dados para mostrar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }

    val maior = barras.maiorValor()

    val precisaLimitar = barras.size > 6
    val modificadorColuna = if (precisaLimitar) modifier.heightIn(max = alturaMaxima) else modifier

    Column(modifier = modificadorColuna) {
        barras.forEach { barra ->
            val fracaoAlvo = if (maior <= 0.0) 0f else (barra.valor / maior).toFloat().coerceIn(0f, 1f)
            val fracaoAnimada by animateFloatAsState(targetValue = fracaoAlvo, label = "fracaoBarra")

            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = barra.rotulo,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 72.dp)
                    )
                    Text(
                        text = formatarMoeda(barra.valor),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fracaoAnimada)
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(barra.cor)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GraficoBarrasPreview() {
    val exemplo = listOf(
        BarraGrafico("Aluguel", 1200.0, com.example.myapplication.ui.theme.AzulFixo),
        BarraGrafico("Alimentacao", 850.0, com.example.myapplication.ui.theme.LaranjaVariavel),
        BarraGrafico("Acoes", 400.0, com.example.myapplication.ui.theme.RoxoInvestimento),
        BarraGrafico("Lazer", 150.0, com.example.myapplication.ui.theme.LaranjaVariavel)
    )
    GraficoBarras(barras = exemplo, modifier = Modifier.fillMaxWidth())
}
