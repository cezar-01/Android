package com.example.myapplication.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LOCALE_BR = Locale("pt", "BR")

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor)

fun formatarPercentual(fracao: Float): String = Math.round(fracao * 100).toString() + "%"

fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", LOCALE_BR).format(Date(millis))

fun textoParaValor(texto: String): Double? {
    val limpo = texto.trim()
        .replace("R$", "")
        .replace(" ", "")
        .replace(".", "")
        .replace(",", ".")
    if (limpo.isEmpty()) return null
    return limpo.toDoubleOrNull()
}

fun valorParaTexto(valor: Double): String =
    if (valor <= 0.0) "" else String.format(LOCALE_BR, "%.2f", valor)
