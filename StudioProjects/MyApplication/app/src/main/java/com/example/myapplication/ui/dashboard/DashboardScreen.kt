package com.example.myapplication.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.charts.FatiaGrafico
import com.example.myapplication.ui.charts.GraficoBarras
import com.example.myapplication.ui.charts.GraficoPizza
import com.example.myapplication.ui.charts.LegendaGrafico
import com.example.myapplication.ui.charts.BarraGrafico
import com.example.myapplication.ui.components.BarraProgresso
import com.example.myapplication.ui.components.CartaoSecao
import com.example.myapplication.ui.components.EstadoVazio
import com.example.myapplication.ui.components.ResumoValor
import com.example.myapplication.ui.theme.PaletaSubcategorias
import com.example.myapplication.ui.theme.VerdePositivo
import com.example.myapplication.ui.theme.VermelhoAlerta
import com.example.myapplication.ui.theme.corDoTipo
import com.example.myapplication.util.formatarMoeda
import com.example.myapplication.util.formatarPercentual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)) {
    val estado by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val saudacao = if (estado.nome.isNotBlank()) "Ola, ${estado.nome}!" else "Resumo financeiro"
            Text(text = saudacao, style = MaterialTheme.typography.headlineSmall)

            CartaoSecao(titulo = "Resumo do mes") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResumoValor(
                        rotulo = "Salario",
                        valor = formatarMoeda(estado.salario),
                        modifier = Modifier.weight(1f)
                    )
                    ResumoValor(
                        rotulo = "Total gasto",
                        valor = formatarMoeda(estado.totalGasto),
                        modifier = Modifier.weight(1f)
                    )
                    ResumoValor(
                        rotulo = "Saldo",
                        valor = formatarMoeda(estado.saldo),
                        cor = if (estado.saldo >= 0) VerdePositivo else VermelhoAlerta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            CartaoSecao(titulo = "Quanto do salario ja foi comprometido") {
                if (estado.salario <= 0) {
                    Text("Cadastre seu salario para ver essa relacao.")
                } else {
                    val estourou = estado.percentualComprometido > 1f
                    BarraProgresso(
                        progresso = estado.percentualComprometido,
                        cor = if (estourou) VermelhoAlerta else MaterialTheme.colorScheme.primary
                    )
                    Text(text = "${formatarPercentual(estado.percentualComprometido)} do salario")
                    if (estourou) {
                        Text(
                            text = "Atencao: seus gastos passaram do salario!",
                            color = VermelhoAlerta
                        )
                    }
                }
            }

            if (!estado.temGastos) {
                EstadoVazio(
                    icone = Icons.Default.PieChart,
                    titulo = "Sem gastos ainda",
                    mensagem = "Cadastre gastos na aba Gastos para ver os graficos aqui."
                )
            } else {
                CartaoSecao(titulo = "Gastos por categoria") {
                    val fatias = estado.porTipo
                        .filter { it.total > 0 }
                        .map { FatiaGrafico(it.tipo.rotulo, it.total, corDoTipo(it.tipo)) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GraficoPizza(
                            fatias = fatias,
                            textoCentral = formatarMoeda(estado.totalGasto),
                            rotuloCentral = "Total"
                        )
                    }
                    LegendaGrafico(fatias = fatias)

                    estado.porTipo.forEach { resumo ->
                        Text(
                            text = "${resumo.tipo.rotulo} consomem ${formatarPercentual(resumo.percentualDoSalario)} do salario",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                CartaoSecao(titulo = "Maiores subdivisoes") {
                    val barras = estado.topSubcategorias.mapIndexed { i, subcategoria ->
                        BarraGrafico(
                            rotulo = subcategoria.nome,
                            valor = subcategoria.total,
                            cor = PaletaSubcategorias[i % PaletaSubcategorias.size]
                        )
                    }
                    GraficoBarras(barras = barras)
                }
            }

            CartaoSecao(titulo = "Guardado em metas") {
                ResumoValor(
                    rotulo = "Total no cofrinho",
                    valor = formatarMoeda(estado.totalGuardadoEmMetas),
                    detalhe = "Investimentos lancados: " + formatarMoeda(estado.totalInvestido)
                )
            }
        }
    }
}
