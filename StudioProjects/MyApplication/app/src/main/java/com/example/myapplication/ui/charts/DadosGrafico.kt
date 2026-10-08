package com.example.myapplication.ui.charts

import androidx.compose.ui.graphics.Color

data class FatiaGrafico(val rotulo: String, val valor: Double, val cor: Color)

data class BarraGrafico(val rotulo: String, val valor: Double, val cor: Color)

fun List<FatiaGrafico>.somaValores(): Double = sumOf { it.valor }

fun List<BarraGrafico>.maiorValor(): Double = maxOfOrNull { it.valor } ?: 0.0
