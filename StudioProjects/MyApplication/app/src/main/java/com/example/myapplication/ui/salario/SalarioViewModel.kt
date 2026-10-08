package com.example.myapplication.ui.salario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import com.example.myapplication.util.textoParaValor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SalarioUiState(
    val nome: String = "",
    val salarioTexto: String = "",
    val erroNome: String? = null,
    val erroSalario: String? = null,
    val salvo: Boolean = false
)

class SalarioViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SalarioUiState())
    val uiState: StateFlow<SalarioUiState> = _uiState.asStateFlow()

    init {
        val perfil = repositorio.perfil.value
        if (perfil.configurado) {
            _uiState.update {
                it.copy(
                    nome = perfil.nome,
                    salarioTexto = perfil.salarioMensal.toString()
                )
            }
        }
    }

    fun aoMudarNome(valor: String) {
        _uiState.update { it.copy(nome = valor, erroNome = null) }
    }

    fun aoMudarSalario(valor: String) {
        _uiState.update { it.copy(salarioTexto = valor, erroSalario = null) }
    }

    fun salvar() {
        val estadoAtual = _uiState.value

        val erroNome = if (estadoAtual.nome.isBlank()) "Digite seu nome" else null

        val valor = textoParaValor(estadoAtual.salarioTexto)
        val erroSalario = when {
            valor == null -> "Valor invalido"
            valor <= 0 -> "O salario precisa ser maior que zero"
            else -> null
        }

        if (erroNome != null || erroSalario != null) {
            _uiState.update { it.copy(erroNome = erroNome, erroSalario = erroSalario) }
            return
        }

        viewModelScope.launch {
            repositorio.salvarPerfil(estadoAtual.nome, valor!!)
            _uiState.update {
                it.copy(erroNome = null, erroSalario = null, salvo = true)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { SalarioViewModel(AppContainer.repositorio) }
        }
    }
}
