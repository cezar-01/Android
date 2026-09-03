package com.example.myapplication.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LOCALE_BR = Locale("pt", "BR")

/** Formata um valor como moeda brasileira: 1234.5 -> "R$ 1.234,50". */
fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor)

/** Formata uma fracao 0f..1f como porcentagem inteira: 0.235f -> "24%". */
fun formatarPercentual(fracao: Float): String = Math.round(fracao * 100).toString() + "%"

/** Formata um timestamp como "03/09/2026". */
fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", LOCALE_BR).format(Date(millis))

/**
 * Le o que o usuario digitou em um campo de dinheiro e devolve Double.
 * Aceita "1.234,56", "1234,56" e "1234.56". Devolve null se nao for um numero valido.
 */
fun textoParaValor(texto: String): Double? {
    val limpo = texto.trim()
        .replace("R$", "")
        .replace(" ", "")
        .replace(".", "")
        .replace(",", ".")
    if (limpo.isEmpty()) return null
    return limpo.toDoubleOrNull()
}
