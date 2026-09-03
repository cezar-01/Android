package com.example.myapplication.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.model.Gasto
import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Um gasto ja pronto pra tela, com o nome da subcategoria resolvido (evita buscar toda hora no Compose).
data class ItemGasto(val gasto: Gasto, val nomeSubcategoria: String)

// Agrupamento dos gastos por tipo (Fixos, Variaveis, Investimentos) com o total ja somado.
data class GrupoGastos(val tipo: TipoGasto, val total: Double, val itens: List<ItemGasto>)

data class GastosUiState(
    val subcategorias: List<Subcategoria> = emptyList(),
    val grupos: List<GrupoGastos> = emptyList(),
    val totalGeral: Double = 0.0,
    val salario: Double = 0.0
)

// Este ViewModel apenas transforma os Flows do repositorio em estado de UI.
// A tela nunca fala com o repositorio diretamente, so observa o uiState e chama estas funcoes.
class GastosViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    val uiState: StateFlow<GastosUiState> = combine(
        repositorio.gastos,
        repositorio.subcategorias,
        repositorio.perfil
    ) { gastos, subs, perfil ->
        // Monta sempre os 3 grupos, na ordem do enum, mesmo que algum fique vazio.
        val grupos = TipoGasto.values().map { tipo ->
            val itensDoTipo = gastos
                .filter { it.tipo == tipo }
                .sortedByDescending { it.dataMillis }
                .map { gasto ->
                    val nome = subs.firstOrNull { it.id == gasto.subcategoriaId }?.nome ?: "Sem categoria"
                    ItemGasto(gasto = gasto, nomeSubcategoria = nome)
                }
            GrupoGastos(
                tipo = tipo,
                total = itensDoTipo.sumOf { it.gasto.valor },
                itens = itensDoTipo
            )
        }

        val subsOrdenadas = subs.sortedWith(compareBy({ it.tipo }, { it.nome }))

        GastosUiState(
            subcategorias = subsOrdenadas,
            grupos = grupos,
            totalGeral = gastos.sumOf { it.valor },
            salario = perfil.salarioMensal
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GastosUiState()
    )

    fun adicionarGasto(descricao: String, valor: Double, subcategoriaId: String) {
        viewModelScope.launch {
            repositorio.adicionarGasto(descricao, valor, subcategoriaId)
        }
    }

    fun removerGasto(id: String) {
        viewModelScope.launch {
            repositorio.removerGasto(id)
        }
    }

    fun adicionarSubcategoria(nome: String, tipo: TipoGasto) {
        viewModelScope.launch {
            repositorio.adicionarSubcategoria(nome, tipo)
        }
    }

    fun removerSubcategoria(id: String) {
        viewModelScope.launch {
            repositorio.removerSubcategoria(id)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { GastosViewModel(AppContainer.repositorio) }
        }
    }
}
