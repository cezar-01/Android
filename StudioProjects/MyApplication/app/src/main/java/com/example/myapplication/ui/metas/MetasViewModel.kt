package com.example.myapplication.ui.metas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.model.Meta
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Estado da tela de metas: lista de metas e totais gerais do cofrinho
data class MetasUiState(
    val metas: List<Meta> = emptyList(),
    val totalGuardado: Double = 0.0,
    val totalAlvo: Double = 0.0,
    val progressoGeral: Float = 0f
)

class MetasViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    val uiState: StateFlow<MetasUiState> = repositorio.metas.map { metas ->
        val totalGuardado = metas.sumOf { it.valorGuardado }
        val totalAlvo = metas.sumOf { it.valorAlvo }
        MetasUiState(
            metas = metas.sortedBy { it.criadaEmMillis },
            totalGuardado = totalGuardado,
            totalAlvo = totalAlvo,
            progressoGeral = if (totalAlvo > 0) {
                (totalGuardado / totalAlvo).toFloat().coerceIn(0f, 1f)
            } else {
                0f
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MetasUiState())

    // Defesa simples: nome em branco ou valor invalido nao cria meta (a tela ja valida antes)
    fun criarMeta(nome: String, valorAlvo: Double) {
        if (nome.isBlank() || valorAlvo <= 0) return
        viewModelScope.launch {
            repositorio.adicionarMeta(nome, valorAlvo)
        }
    }

    fun removerMeta(id: String) {
        viewModelScope.launch {
            repositorio.removerMeta(id)
        }
    }

    fun guardar(metaId: String, valor: Double) {
        viewModelScope.launch {
            repositorio.depositarNaMeta(metaId, valor)
        }
    }

    fun retirar(metaId: String, valor: Double) {
        viewModelScope.launch {
            repositorio.retirarDaMeta(metaId, valor)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MetasViewModel(AppContainer.repositorio) }
        }
    }
}
