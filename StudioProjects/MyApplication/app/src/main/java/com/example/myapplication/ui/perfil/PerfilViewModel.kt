package com.example.myapplication.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    /** Rascunho do nome enquanto o usuario digita (so vai para o repositorio ao salvar). */
    val nome: String = "",
    val fotoUri: String? = null,
    val salarioMensal: Double = 0.0,
    val erroNome: String? = null,
    /** Mensagem de confirmacao mostrada uma vez e depois limpa pela tela. */
    val mensagem: String? = null
)

/**
 * ViewModel da aba Perfil. Mantem o nome como rascunho local (so grava quando o usuario
 * clica em Salvar) e reflete a foto direto do repositorio, porque a troca de foto e imediata.
 */
class PerfilViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Nome e salario iniciais vem do que ja foi cadastrado na primeira tela.
        val perfilAtual = repositorio.perfil.value
        _uiState.update {
            it.copy(
                nome = perfilAtual.nome,
                fotoUri = perfilAtual.fotoUri,
                salarioMensal = perfilAtual.salarioMensal
            )
        }
        // A foto e o salario continuam sendo observados para a tela nunca ficar desatualizada.
        viewModelScope.launch {
            repositorio.perfil.collect { perfil ->
                _uiState.update {
                    it.copy(fotoUri = perfil.fotoUri, salarioMensal = perfil.salarioMensal)
                }
            }
        }
    }

    fun aoMudarNome(valor: String) {
        _uiState.update { it.copy(nome = valor, erroNome = null) }
    }

    fun salvarNome() {
        val nome = _uiState.value.nome.trim()
        if (nome.isBlank()) {
            _uiState.update { it.copy(erroNome = "Digite seu nome") }
            return
        }
        viewModelScope.launch {
            repositorio.atualizarNome(nome)
            _uiState.update { it.copy(erroNome = null, mensagem = "Nome atualizado") }
        }
    }

    fun definirFoto(uri: String) {
        viewModelScope.launch {
            repositorio.atualizarFotoPerfil(uri)
            _uiState.update { it.copy(mensagem = "Foto atualizada") }
        }
    }

    fun removerFoto() {
        viewModelScope.launch {
            repositorio.atualizarFotoPerfil(null)
            _uiState.update { it.copy(mensagem = "Foto removida") }
        }
    }

    /** A tela chama isso depois de exibir a mensagem, para ela nao aparecer de novo. */
    fun mensagemMostrada() {
        _uiState.update { it.copy(mensagem = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { PerfilViewModel(AppContainer.repositorio) }
        }
    }
}
