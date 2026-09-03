package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * IMPORTANTE: cada papel do esquema precisa de uma cor PROPRIA.
 * O Material 3 descobre a cor do texto de um Card com `contentColorFor(containerColor)`,
 * que compara as cores por valor. Se `secondary` e `surfaceVariant` forem a mesma cor,
 * ele acha `secondary` primeiro e usa `onSecondary` (branco) dentro dos cards, deixando
 * o texto ilegivel. Por isso os tons abaixo sao todos distintos e cada "on*" e explicito.
 */

// Esquema claro: fundo areia, cartoes quase brancos com texto escuro.
private val EsquemaClaro = lightColorScheme(
    primary = VerdeEscuro,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCDE9DA),
    onPrimaryContainer = VerdeEscuro,
    secondary = Color(0xFF3F7358),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCEBE1),
    onSecondaryContainer = VerdeEscuro,
    background = AreiaClara,
    onBackground = CinzaTexto,
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF2C332E),
    surfaceVariant = Color(0xFFE7EFE7),
    onSurfaceVariant = Color(0xFF41504A),
    outline = Color(0xFF8A9A91),
    error = VermelhoAlerta,
    onError = Color(0xFFFFFFFF)
)

// Esquema escuro: fundo verde-petroleo bem escuro, cartoes um tom acima do fundo.
private val EsquemaEscuro = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Color(0xFF07281A),
    primaryContainer = Color(0xFF2A5344),
    onPrimaryContainer = Color(0xFFCDE9DA),
    secondary = Color(0xFF9CCFB4),
    onSecondary = Color(0xFF0B2318),
    secondaryContainer = Color(0xFF2F4A3C),
    onSecondaryContainer = Color(0xFFDCEBE1),
    background = Color(0xFF111A15),
    onBackground = Color(0xFFE7EFE7),
    surface = Color(0xFF16211B),
    onSurface = Color(0xFFE7EFE7),
    surfaceVariant = Color(0xFF24322A),
    onSurfaceVariant = Color(0xFFC3D5C9),
    outline = Color(0xFF7C8D84),
    error = Color(0xFFEF8A82),
    onError = Color(0xFF3A0906)
)

// Tema principal do app. Nao usamos dynamicColor para manter a identidade visual
// propria da Calculadora de Gastos em qualquer versao do Android.
@Composable
fun CalculadoraGastosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquemaCores = if (darkTheme) EsquemaEscuro else EsquemaClaro

    val view = LocalView.current
    if (!view.isInEditMode) {
        val contexto = LocalContext.current
        SideEffect {
            val activity = contexto as? Activity
            val window = activity?.window ?: return@SideEffect
            window.statusBarColor = esquemaCores.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = esquemaCores,
        typography = Tipografia,
        content = content
    )
}
