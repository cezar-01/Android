package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// "Stat tile": rotulo pequeno em cima, valor grande em negrito embaixo e um
// detalhe opcional ainda menor. O valor ja chega formatado de quem chama.
@Composable
fun ResumoValor(
    rotulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    cor: Color = MaterialTheme.colorScheme.onSurface,
    detalhe: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = cor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (detalhe != null) {
            Text(
                text = detalhe,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResumoValorPreview() {
    Surface {
        Row(modifier = Modifier.padding(16.dp)) {
            ResumoValor(
                rotulo = "Salario",
                valor = "R$ 5.000,00",
                modifier = Modifier.width(110.dp)
            )
            ResumoValor(
                rotulo = "Total gasto",
                valor = "R$ 3.245,90",
                modifier = Modifier.width(110.dp),
                cor = Color(0xFFB00020)
            )
            ResumoValor(
                rotulo = "Saldo",
                valor = "R$ 1.754,10",
                modifier = Modifier.width(110.dp),
                cor = Color(0xFF2E7D32),
                detalhe = "35% comprometido"
            )
        }
    }
}
