package com.example.myapplication.ui.gastos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.TipoGasto
import com.example.myapplication.ui.components.CartaoSecao
import com.example.myapplication.ui.theme.corDoTipo
import com.example.myapplication.util.formatarData
import com.example.myapplication.util.formatarMoeda
import com.example.myapplication.util.formatarPercentual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(viewModel: GastosViewModel = viewModel(factory = GastosViewModel.Factory)) {
    val estado by viewModel.uiState.collectAsState()

    var mostrarDialogoGasto by remember { mutableStateOf(false) }
    var mostrarDialogoSubcategoria by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meus gastos") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Gasto") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { mostrarDialogoGasto = true }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CartaoSecao(titulo = "Total gasto") {
                    Text(
                        text = formatarMoeda(estado.totalGeral),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (estado.salario > 0) {
                        val fracao = (estado.totalGeral / estado.salario).toFloat()
                        Text(
                            text = "${formatarPercentual(fracao)} do seu salario de ${formatarMoeda(estado.salario)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { mostrarDialogoSubcategoria = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nova subdivisao")
                }
            }

            items(estado.grupos) { grupo ->
                CartaoSecao(
                    titulo = grupo.tipo.rotulo,
                    subtitulo = formatarMoeda(grupo.total),
                    acao = {
                        Box(modifier = Modifier.size(10.dp)) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = corDoTipo(grupo.tipo))
                            }
                        }
                    }
                ) {
                    if (grupo.itens.isEmpty()) {
                        Text(
                            text = "Nenhum gasto lancado aqui ainda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        grupo.itens.forEachIndexed { indice, item ->
                            LinhaItemGasto(
                                item = item,
                                aoRemover = { viewModel.removerGasto(item.gasto.id) }
                            )
                            if (indice < grupo.itens.lastIndex) {
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoGasto) {
            DialogoNovoGasto(
                subcategorias = estado.subcategorias,
                aoConfirmar = { descricao, valor, subcategoriaId ->
                    viewModel.adicionarGasto(descricao, valor, subcategoriaId)
                    mostrarDialogoGasto = false
                },
                aoFechar = { mostrarDialogoGasto = false }
            )
        }

        if (mostrarDialogoSubcategoria) {
            DialogoNovaSubcategoria(
                tipoInicial = TipoGasto.VARIAVEL,
                aoConfirmar = { nome, tipo ->
                    viewModel.adicionarSubcategoria(nome, tipo)
                    mostrarDialogoSubcategoria = false
                },
                aoFechar = { mostrarDialogoSubcategoria = false }
            )
        }
    }
}

@Composable
private fun LinhaItemGasto(item: ItemGasto, aoRemover: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.gasto.descricao,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${item.nomeSubcategoria} • ${formatarData(item.gasto.dataMillis)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatarMoeda(item.gasto.valor),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = aoRemover) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Remover gasto")
        }
    }
}
