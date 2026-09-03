package com.example.myapplication.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import com.example.myapplication.util.textoParaValor
import com.example.myapplication.util.valorParaTexto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    /** Rascunhos editados na tela; so vao para o repositorio quando o usuario salva. */
    val nome: String = "",
    val salarioTexto: String = "",
    val fotoUri: String? = null,
    val erroNome: String? = null,
    val erroSalario: String? = null,
    /** Mensagem de confirmacao mostrada uma vez e depois limpa pela tela. */
    val mensagem: String? = null
)

/**
 * ViewModel da aba Perfil. Nome e salario sao rascunhos locais (so gravam no botao Salvar),
 * enquanto a foto vem direto do repositorio, porque a troca de foto e imediata.
 */
class PerfilViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Valores iniciais vem do que ja foi cadastrado na primeira tela do app.
        val perfilAtual = repositorio.perfil.value
        _uiState.update {
            it.copy(
                nome = perfilAtual.nome,
                salarioTexto = valorParaTexto(perfilAtual.salarioMensal),
                fotoUri = perfilAtual.fotoUri
            )
        }
        // A foto continua sendo observada para a tela nunca ficar desatualizada.
        viewModelScope.launch {
            repositorio.perfil.collect { perfil ->
                _uiState.update { it.copy(fotoUri = perfil.fotoUri) }
            }
        }
    }

    fun aoMudarNome(valor: String) {
        _uiState.update { it.copy(nome = valor, erroNome = null) }
    }

    fun aoMudarSalario(valor: String) {
        _uiState.update { it.copy(salarioTexto = valor, erroSalario = null) }
    }

    /**
     * Grava nome e salario de uma vez. Usa salvarPerfil(), que faz copy() no perfil e
     * por isso preserva a foto ja escolhida.
     */
    fun salvar() {
        val nome = _uiState.value.nome.trim()
        val salario = textoParaValor(_uiState.value.salarioTexto)

        val erroNome = if (nome.isBlank()) "Digite seu nome" else null
        val erroSalario = when {
            salario == null -> "Valor invalido"
            salario <= 0.0 -> "O salario precisa ser maior que zero"
            else -> null
        }

        if (erroNome != null || erroSalario != null) {
            _uiState.update { it.copy(erroNome = erroNome, erroSalario = erroSalario) }
            return
        }

        viewModelScope.launch {
            repositorio.salvarPerfil(nome, salario!!)
            _uiState.update {
                it.copy(erroNome = null, erroSalario = null, mensagem = "Dados atualizados")
            }
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
