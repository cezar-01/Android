package com.example.myapplication.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.CampoDinheiro
import com.example.myapplication.util.textoParaValor

// Dialogo para criar uma nova meta (cofrinho/caixinha) com nome e valor alvo.
@Composable
fun DialogoNovaMeta(
    aoConfirmar: (nome: String, valorAlvo: Double) -> Unit,
    aoFechar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }

    // So aceita criar a meta se o nome nao estiver vazio e o valor alvo for valido e positivo
    val alvo = textoParaValor(valorTexto)?.takeIf { it > 0.0 }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Nova meta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Crie uma caixinha para guardar dinheiro aos poucos ate juntar o valor " +
                        "que voce precisa para o seu objetivo.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da meta") },
                    placeholder = { Text("Ex.: Viagem para a praia") },
                    singleLine = true
                )
                CampoDinheiro(
                    valor = valorTexto,
                    aoMudar = { valorTexto = it },
                    rotulo = "Quanto custa"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { aoConfirmar(nome.trim(), alvo!!) },
                enabled = nome.isNotBlank() && alvo != null
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
