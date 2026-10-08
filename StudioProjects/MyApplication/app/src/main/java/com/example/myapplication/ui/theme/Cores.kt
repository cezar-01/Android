package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.model.TipoGasto

val VerdeEscuro = Color(0xFF1B4332)
val VerdeClaro = Color(0xFF74C69D)
val AreiaClara = Color(0xFFF3EDE3)
val CinzaTexto = Color(0xFF4A4A4A)

val AzulFixo = Color(0xFF3A6EA5)
val LaranjaVariavel = Color(0xFFE0862B)
val RoxoInvestimento = Color(0xFF7B5EA7)

val VermelhoAlerta = Color(0xFFC1443C)
val VerdePositivo = Color(0xFF2D8A5B)

val PaletaSubcategorias = listOf(
    Color(0xFF3A6EA5),
    Color(0xFFE0862B),
    Color(0xFF7B5EA7),
    Color(0xFF2D8A5B),
    Color(0xFFC1443C),
    Color(0xFF2FA6A6),
    Color(0xFFB08900),
    Color(0xFFA3487A),
    Color(0xFF5C6BC0),
    Color(0xFF8D6E63)
)

fun corDoTipo(tipo: TipoGasto): Color = when (tipo) {
    TipoGasto.FIXO -> AzulFixo
    TipoGasto.VARIAVEL -> LaranjaVariavel
    TipoGasto.INVESTIMENTO -> RoxoInvestimento
}
