package com.example.myapplication.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.charts.FatiaGrafico
import com.example.myapplication.ui.charts.GraficoPizza
import com.example.myapplication.ui.components.CartaoSecao
import com.example.myapplication.ui.components.EstadoVazio
import com.example.myapplication.ui.components.ResumoValor
import com.example.myapplication.ui.theme.VerdePositivo
import com.example.myapplication.ui.theme.VermelhoAlerta
import com.example.myapplication.util.formatarData
import com.example.myapplication.util.formatarMoeda
import com.example.myapplication.util.formatarPercentual

// Tela do "porquinho": detalhe de uma meta, com progresso e historico de movimentacoes.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaDetalheScreen(
    metaId: String,
    aoVoltar: () -> Unit,
    viewModel: MetaDetalheViewModel = viewModel(factory = MetaDetalheViewModel.factory(metaId))
) {
    val estado by viewModel.uiState.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(estado.meta?.nome ?: "Meta") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        val meta = estado.meta
        if (meta == null) {
            EstadoVazio(
                icone = Icons.Default.SearchOff,
                titulo = "Meta nao encontrada",
                mensagem = "Essa meta pode ter sido removida.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Porquinho: anel de progresso com o quanto ja foi guardado.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GraficoPizza(
                        fatias = listOf(
                            FatiaGrafico("Guardado", meta.valorGuardado, VerdePositivo),
                            FatiaGrafico("Falta", meta.valorRestante, Color.LightGray)
                        ),
                        tamanho = 200.dp,
                        textoCentral = formatarPercentual(meta.progresso),
                        rotuloCentral = "guardado"
                    )
                }

                CartaoSecao(titulo = "Resumo") {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ResumoValor(
                            rotulo = "Guardado",
                            valor = formatarMoeda(meta.valorGuardado),
                            modifier = Modifier.weight(1f),
                            cor = VerdePositivo
                        )
                        ResumoValor(
                            rotulo = "Falta",
                            valor = formatarMoeda(meta.valorRestante),
                            modifier = Modifier.weight(1f)
                        )
                        ResumoValor(
                            rotulo = "Objetivo",
                            valor = formatarMoeda(meta.valorAlvo),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Button(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Savings, contentDescription = null)
                    Text(text = "  Guardar dinheiro")
                }

                CartaoSecao(titulo = "Historico") {
                    if (estado.movimentacoes.isEmpty()) {
                        Text(
                            text = "Nenhuma movimentacao ainda.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            estado.movimentacoes.forEach { mov ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatarData(mov.dataMillis),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    val positivo = mov.valor > 0
                                    val texto = if (positivo) {
                                        "+" + formatarMoeda(mov.valor)
                                    } else {
                                        formatarMoeda(mov.valor)
                                    }
                                    Text(
                                        text = texto,
                                        color = if (positivo) VerdePositivo else VermelhoAlerta,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (mostrarDialogo) {
                DialogoGuardarDinheiro(
                    nomeMeta = meta.nome,
                    aoConfirmar = { valor, ehDeposito ->
                        if (ehDeposito) {
                            viewModel.guardar(valor)
                        } else {
                            viewModel.retirar(valor)
                        }
                        mostrarDialogo = false
                    },
                    aoFechar = { mostrarDialogo = false }
                )
            }
        }
    }
}
