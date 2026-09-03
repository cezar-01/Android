package com.example.myapplication.ui.charts

import androidx.compose.ui.graphics.Color

// Estas classes desacoplam os graficos do restante do app: um grafico de pizza ou de
// barras nao precisa saber o que e um Gasto, uma Subcategoria ou uma Meta -- ele so
// recebe um rotulo (texto), um valor (numero) e uma cor para desenhar. Quem monta a
// tela (Dashboard, MetaDetalhe, etc.) e responsavel por converter os dados de dominio
// nessas formas simples antes de passar para os composables de grafico.

/** Uma fatia do grafico de pizza/rosca: rotulo exibido, valor numerico e cor do desenho. */
data class FatiaGrafico(val rotulo: String, val valor: Double, val cor: Color)

/** Uma barra do grafico de barras horizontais: rotulo exibido, valor numerico e cor do desenho. */
data class BarraGrafico(val rotulo: String, val valor: Double, val cor: Color)

/** Soma os valores de todas as fatias -- usado para calcular percentuais na legenda. */
fun List<FatiaGrafico>.somaValores(): Double = sumOf { it.valor }

/** Maior valor entre as barras -- usado para dimensionar a barra mais longa como 100%. */
fun List<BarraGrafico>.maiorValor(): Double = maxOfOrNull { it.valor } ?: 0.0
