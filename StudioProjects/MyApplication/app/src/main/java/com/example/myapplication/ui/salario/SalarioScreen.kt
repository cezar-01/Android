package com.example.myapplication.ui.salario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.CampoDinheiro

@Composable
fun SalarioScreen(
    aoConcluir: () -> Unit,
    viewModel: SalarioViewModel = viewModel(factory = SalarioViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(estado.salvo) {
        if (estado.salvo) {
            aoConcluir()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        Text(
            text = "Calculadora de Gastos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        Text(
            text = "Organize seus gastos em Fixos, Variaveis e Investimentos, e " +
                "compare tudo com o seu salario mensal.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        OutlinedTextField(
            value = estado.nome,
            onValueChange = viewModel::aoMudarNome,
            label = { Text("Seu nome") },
            singleLine = true,
            isError = estado.erroNome != null,
            supportingText = {
                estado.erroNome?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        CampoDinheiro(
            valor = estado.salarioTexto,
            aoMudar = viewModel::aoMudarSalario,
            rotulo = "Salario mensal",
            erro = estado.erroSalario,
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        Text(
            text = "O salario e a base dos percentuais mostrados no dashboard. " +
                "Voce pode alterar esse valor depois.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        Button(
            onClick = { viewModel.salvar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comecar")
        }
    }
}
