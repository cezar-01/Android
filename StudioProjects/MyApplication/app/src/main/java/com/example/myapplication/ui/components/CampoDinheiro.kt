package com.example.myapplication.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

// Campo de texto para valores em dinheiro: mostra "R$" na frente e so aceita
// digitos, virgula e ponto, limitando o tamanho para nao deixar o usuario digitar bobagem.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDinheiro(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    erro: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { novo ->
            val filtrado = novo.filter { it.isDigit() || it == ',' || it == '.' }
            val limitado = if (filtrado.length > 12) filtrado.take(12) else filtrado
            aoMudar(limitado)
        },
        modifier = modifier,
        label = { Text(rotulo) },
        leadingIcon = { Text("R$") },
        singleLine = true,
        isError = erro != null,
        supportingText = {
            if (erro != null) {
                Text(text = erro, color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoDinheiroPreview() {
    CampoDinheiro(
        valor = "150,00",
        aoMudar = {},
        rotulo = "Salario mensal",
        erro = null
    )
}
