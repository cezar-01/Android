package com.example.myapplication.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.CampoDinheiro
import com.example.myapplication.util.textoParaValor

// Dialogo usado para guardar ou retirar dinheiro de uma meta (cofrinho).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoGuardarDinheiro(
    nomeMeta: String,
    aoConfirmar: (valor: Double, ehDeposito: Boolean) -> Unit,
    aoFechar: () -> Unit
) {
    var ehDeposito by remember { mutableStateOf(true) }
    var valorTexto by remember { mutableStateOf("") }

    // So habilita o botao de confirmar se o valor digitado for valido e positivo
    val valor = textoParaValor(valorTexto)?.takeIf { it > 0.0 }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Cofrinho: $nomeMeta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = ehDeposito,
                        onClick = { ehDeposito = true },
                        label = { Text("Guardar") }
                    )
                    FilterChip(
                        selected = !ehDeposito,
                        onClick = { ehDeposito = false },
                        label = { Text("Retirar") }
                    )
                }
                CampoDinheiro(
                    valor = valorTexto,
                    aoMudar = { valorTexto = it },
                    rotulo = if (ehDeposito) "Quanto guardar" else "Quanto retirar"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { aoConfirmar(valor!!, ehDeposito) },
                enabled = valor != null
            ) {
                Text(if (ehDeposito) "Guardar" else "Retirar")
            }
        },
        dismissButton = {
            TextButton(onClick = aoFechar) {
                Text("Cancelar")
            }
        }
    )
}
