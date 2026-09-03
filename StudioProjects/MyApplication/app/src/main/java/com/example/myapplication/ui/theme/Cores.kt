package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.model.TipoGasto

// Paleta principal do app: tema financeiro sobrio, baseado em verdes e areia.
val VerdeEscuro = Color(0xFF1B4332)
val VerdeClaro = Color(0xFF74C69D)
val AreiaClara = Color(0xFFF3EDE3)
val CinzaTexto = Color(0xFF4A4A4A)

// Cores fixas por tipo de gasto (usadas em graficos e destaques).
val AzulFixo = Color(0xFF3A6EA5)
val LaranjaVariavel = Color(0xFFE0862B)
val RoxoInvestimento = Color(0xFF7B5EA7)

// Cores de estado (alertas e valores positivos).
val VermelhoAlerta = Color(0xFFC1443C)
val VerdePositivo = Color(0xFF2D8A5B)

// Paleta usada para diferenciar subcategorias em graficos (pizza/barras).
// Sao 10 cores distintas e legiveis tanto em fundo claro quanto escuro.
val PaletaSubcategorias = listOf(
    Color(0xFF3A6EA5), // azul
    Color(0xFFE0862B), // laranja
    Color(0xFF7B5EA7), // roxo
    Color(0xFF2D8A5B), // verde
    Color(0xFFC1443C), // vermelho
    Color(0xFF2FA6A6), // ciano
    Color(0xFFB08900), // dourado
    Color(0xFFA3487A), // magenta
    Color(0xFF5C6BC0), // indigo
    Color(0xFF8D6E63)  // marrom
)

// Mapeia cada tipo de gasto para a sua cor de destaque no app.
fun corDoTipo(tipo: TipoGasto): Color = when (tipo) {
    TipoGasto.FIXO -> AzulFixo
    TipoGasto.VARIAVEL -> LaranjaVariavel
    TipoGasto.INVESTIMENTO -> RoxoInvestimento
}
