package com.example.myapplication.ui.gastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.TipoGasto

// Dialogo para criar uma nova subdivisao (categoria detalhada) dentro de um dos 3 tipos de gasto.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNovaSubcategoria(
    tipoInicial: TipoGasto,
    aoConfirmar: (nome: String, tipo: TipoGasto) -> Unit,
    aoFechar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(tipoInicial) }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Nova subdivisao") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Subdivisoes sao as categorias detalhadas dentro de Fixos, Variaveis e " +
                        "Investimentos (ex.: Lazer, Alimentação, Poupança)."
                )
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da subdivisao") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoGasto.values().forEach { opcao ->
                        FilterChip(
                            selected = tipo == opcao,
                            onClick = { tipo = opcao },
                            label = { Text(opcao.rotulo) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { aoConfirmar(nome.trim(), tipo) },
                enabled = nome.isNotBlank()
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = aoFechar) {
                Text("Cancelar")
            }
        }
    )
}
