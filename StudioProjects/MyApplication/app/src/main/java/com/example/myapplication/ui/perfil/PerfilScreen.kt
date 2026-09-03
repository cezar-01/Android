package com.example.myapplication.ui.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.components.CartaoSecao
import com.example.myapplication.ui.components.CampoDinheiro

/**
 * Aba de Perfil: o usuario troca o nome, o salario mensal e escolhe uma foto da galeria.
 * Nao e um cadastro nem login, e so a identificacao simples usada no dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)) {
    val estado by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Photo Picker do Android: nao precisa de permissao de armazenamento.
    val selecionarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) viewModel.definirFoto(uri.toString())
    }

    // Mostra a confirmacao uma unica vez e avisa o ViewModel para limpar a mensagem.
    LaunchedEffect(estado.mensagem) {
        val mensagem = estado.mensagem
        if (mensagem != null) {
            snackbarHostState.showSnackbar(mensagem)
            viewModel.mensagemMostrada()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarPerfil(fotoUri = estado.fotoUri)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        selecionarFoto.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (estado.fotoUri == null) "Escolher foto" else "Trocar foto")
                }
                if (estado.fotoUri != null) {
                    OutlinedButton(onClick = { viewModel.removerFoto() }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remover foto")
                    }
                }
            }

            CartaoSecao(
                titulo = "Seus dados",
                subtitulo = "Usado para personalizar o dashboard"
            ) {
                OutlinedTextField(
                    value = estado.nome,
                    onValueChange = viewModel::aoMudarNome,
                    label = { Text("Seu nome") },
                    singleLine = true,
                    isError = estado.erroNome != null,
                    supportingText = estado.erroNome?.let {
                        { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoDinheiro(
                    valor = estado.salarioTexto,
                    aoMudar = viewModel::aoMudarSalario,
                    rotulo = "Salario mensal",
                    erro = estado.erroSalario,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Mudar o salario recalcula na hora os percentuais do dashboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.salvar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar alteracoes")
                }
            }
        }
    }
}

/** Foto redonda do usuario; sem foto, mostra um avatar padrao com icone de pessoa. */
@Composable
private fun AvatarPerfil(fotoUri: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (fotoUri != null) {
            AsyncImage(
                model = fotoUri,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Sem foto de perfil",
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sem foto",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
