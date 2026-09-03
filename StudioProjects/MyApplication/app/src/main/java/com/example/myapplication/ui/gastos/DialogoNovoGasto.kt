package com.example.myapplication.ui.gastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto
import com.example.myapplication.ui.components.CampoDinheiro
import com.example.myapplication.util.textoParaValor

// Dialogo para lancar um novo gasto: descricao, valor, tipo (categoria) e subdivisao.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNovoGasto(
    subcategorias: List<Subcategoria>,
    aoConfirmar: (descricao: String, valor: Double, subcategoriaId: String) -> Unit,
    aoFechar: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf(TipoGasto.VARIAVEL) }
    var subcategoriaSelecionadaId by remember { mutableStateOf<String?>(null) }

    // Valor numerico valido (maior que zero) digitado pelo usuario.
    val valorValido = textoParaValor(valorTexto)?.takeIf { it > 0.0 }

    // Apenas as subcategorias do tipo escolhido aparecem para selecao.
    val subcategoriasFiltradas = subcategorias.filter { it.tipo == tipoSelecionado }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Novo gasto") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descricao (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                CampoDinheiro(
                    valor = valorTexto,
                    aoMudar = { valorTexto = it },
                    rotulo = "Valor"
                )

                Text("Categoria", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoGasto.values().forEach { tipo ->
                        FilterChip(
                            selected = tipo == tipoSelecionado,
                            onClick = {
                                tipoSelecionado = tipo
                                // Troca de categoria invalida a subcategoria escolhida antes.
                                subcategoriaSelecionadaId = null
                            },
                            label = { Text(tipo.rotulo) }
                        )
                    }
                }

                Text("Subdivisao", style = MaterialTheme.typography.labelMedium)
                if (subcategoriasFiltradas.isEmpty()) {
                    Text(
                        "Nenhuma subdivisao para esta categoria ainda. Crie uma nova subdivisao antes de continuar.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    // LazyRow evita a API experimental extra do FlowRow.
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(subcategoriasFiltradas) { subcategoria ->
                            FilterChip(
                                selected = subcategoria.id == subcategoriaSelecionadaId,
                                onClick = { subcategoriaSelecionadaId = subcategoria.id },
                                label = { Text(subcategoria.nome) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val id = subcategoriaSelecionadaId
                    if (valorValido != null && id != null) {
                        aoConfirmar(descricao, valorValido, id)
                    }
                },
                enabled = valorValido != null && subcategoriaSelecionadaId != null
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = aoFechar) {
                Text("Cancelar")
            }
        }
    )
}
