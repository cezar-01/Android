package com.example.myapplication.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Meta
import com.example.myapplication.ui.components.BarraProgresso
import com.example.myapplication.ui.components.CartaoSecao
import com.example.myapplication.ui.components.EstadoVazio
import com.example.myapplication.ui.components.ResumoValor
import com.example.myapplication.ui.theme.VerdePositivo
import com.example.myapplication.util.formatarMoeda
import com.example.myapplication.util.formatarPercentual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(
    aoAbrirMeta: (String) -> Unit,
    viewModel: MetasViewModel = viewModel(factory = MetasViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()

    var mostrarNovaMeta by remember { mutableStateOf(false) }
    var metaParaGuardar by remember { mutableStateOf<Meta?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Minhas metas") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nova meta") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { mostrarNovaMeta = true }
            )
        }
    ) { innerPadding ->
        if (estado.metas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EstadoVazio(
                    icone = Icons.Default.Savings,
                    titulo = "Nenhuma meta ainda",
                    mensagem = "Crie uma meta (ex.: Viagem de R$ 10.000) e va guardando dinheiro aos poucos."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CartaoSecao(titulo = "Cofrinho geral") {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ResumoValor(
                                rotulo = "Guardado",
                                valor = formatarMoeda(estado.totalGuardado),
                                modifier = Modifier.weight(1f)
                            )
                            ResumoValor(
                                rotulo = "Objetivo",
                                valor = formatarMoeda(estado.totalAlvo),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        BarraProgresso(
                            progresso = estado.progressoGeral,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }

                items(estado.metas, key = { it.id }) { meta ->
                    Card(onClick = { aoAbrirMeta(meta.id) }) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = meta.nome, style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { viewModel.removerMeta(meta.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remover meta")
                                }
                            }

                            Text(
                                text = "${formatarMoeda(meta.valorGuardado)} de ${formatarMoeda(meta.valorAlvo)}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            BarraProgresso(
                                progresso = meta.progresso,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )

                            Text(
                                text = formatarPercentual(meta.progresso),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (meta.concluida) {
                                Text(
                                    text = "Meta concluida!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VerdePositivo,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "Faltam ${formatarMoeda(meta.valorRestante)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Button(
                                onClick = { metaParaGuardar = meta },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarNovaMeta) {
        DialogoNovaMeta(
            aoConfirmar = { nome, valorAlvo ->
                viewModel.criarMeta(nome, valorAlvo)
                mostrarNovaMeta = false
            },
            aoFechar = { mostrarNovaMeta = false }
        )
    }

    metaParaGuardar?.let { meta ->
        DialogoGuardarDinheiro(
            nomeMeta = meta.nome,
            aoConfirmar = { valor, ehDeposito ->
                if (ehDeposito) viewModel.guardar(meta.id, valor) else viewModel.retirar(meta.id, valor)
                metaParaGuardar = null
            },
            aoFechar = { metaParaGuardar = null }
        )
    }
}
