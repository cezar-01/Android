package com.example.myapplication.ui.metas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.model.Deposito
import com.example.myapplication.data.model.Meta
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Estado da tela de detalhe: a meta em si e o historico de movimentacoes dela.
data class MetaDetalheUiState(
    val meta: Meta? = null,
    val movimentacoes: List<Deposito> = emptyList() // mais recentes primeiro
)

class MetaDetalheViewModel(
    private val repositorio: FinancasRepository,
    private val metaId: String
) : ViewModel() {

    val uiState: StateFlow<MetaDetalheUiState> = combine(
        repositorio.metas,
        repositorio.depositos
    ) { metas, deps ->
        MetaDetalheUiState(
            meta = metas.firstOrNull { it.id == metaId },
            movimentacoes = deps.filter { it.metaId == metaId }.sortedByDescending { it.dataMillis }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MetaDetalheUiState())

    // Guarda dinheiro na meta atual.
    fun guardar(valor: Double) {
        viewModelScope.launch {
            repositorio.depositarNaMeta(metaId, valor)
        }
    }

    // Retira dinheiro da meta atual.
    fun retirar(valor: Double) {
        viewModelScope.launch {
            repositorio.retirarDaMeta(metaId, valor)
        }
    }

    companion object {
        fun factory(metaId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer { MetaDetalheViewModel(AppContainer.repositorio, metaId) }
        }
    }
}
