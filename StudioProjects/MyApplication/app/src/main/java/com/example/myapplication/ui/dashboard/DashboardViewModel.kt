package com.example.myapplication.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.model.TipoGasto
import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Resumo de um tipo de gasto (Fixo, Variavel, Investimento) para os graficos do dashboard.
data class ResumoTipo(
    val tipo: TipoGasto,
    val total: Double,
    val percentualDoTotal: Float,
    val percentualDoSalario: Float
)

// Resumo de uma subcategoria, usado no ranking das maiores subdivisoes.
data class ResumoSubcategoria(
    val nome: String,
    val tipo: TipoGasto,
    val total: Double,
    val percentualDoTotal: Float
)

data class DashboardUiState(
    val nome: String = "",
    val salario: Double = 0.0,
    val totalGasto: Double = 0.0,
    val saldo: Double = 0.0,
    val percentualComprometido: Float = 0f,
    val totalInvestido: Double = 0.0,
    val totalGuardadoEmMetas: Double = 0.0,
    val porTipo: List<ResumoTipo> = emptyList(),
    val topSubcategorias: List<ResumoSubcategoria> = emptyList(),
    val temGastos: Boolean = false
)

// ViewModel do dashboard: junta perfil, gastos, subcategorias e metas para montar os
// numeros e graficos da tela principal.
class DashboardViewModel(private val repositorio: FinancasRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repositorio.perfil,
        repositorio.gastos,
        repositorio.subcategorias,
        repositorio.metas
    ) { perfil, gastos, subcategorias, metas ->
        val salario = perfil.salarioMensal
        val totalGasto = gastos.sumOf { it.valor }
        val saldo = salario - totalGasto

        // Nao limitamos a 1f de proposito: a tela precisa avisar quando passar de 100%.
        val percentualComprometido = if (salario > 0) (totalGasto / salario).toFloat() else 0f

        // Resumo por tipo, sempre os 3 valores do enum, na ordem declarada.
        val porTipo = TipoGasto.values().map { tipo ->
            val totalDoTipo = gastos.filter { it.tipo == tipo }.sumOf { it.valor }
            val percentualDoTotal = if (totalGasto == 0.0) 0f else (totalDoTipo / totalGasto).toFloat()
            val percentualDoSalario = if (salario <= 0.0) 0f else (totalDoTipo / salario).toFloat()
            ResumoTipo(
                tipo = tipo,
                total = totalDoTipo,
                percentualDoTotal = percentualDoTotal,
                percentualDoSalario = percentualDoSalario
            )
        }

        val totalInvestido = porTipo.firstOrNull { it.tipo == TipoGasto.INVESTIMENTO }?.total ?: 0.0
        val totalGuardadoEmMetas = metas.sumOf { it.valorGuardado }

        // Agrupa os gastos por subcategoria, resolve o nome e ordena do maior para o menor.
        val topSubcategorias = gastos
            .groupBy { it.subcategoriaId }
            .map { (subcategoriaId, gastosDaSubcategoria) ->
                val subcategoria = subcategorias.firstOrNull { it.id == subcategoriaId }
                val total = gastosDaSubcategoria.sumOf { it.valor }
                val percentualDoTotal = if (totalGasto == 0.0) 0f else (total / totalGasto).toFloat()
                ResumoSubcategoria(
                    nome = subcategoria?.nome ?: "Outros",
                    tipo = subcategoria?.tipo ?: gastosDaSubcategoria.first().tipo,
                    total = total,
                    percentualDoTotal = percentualDoTotal
                )
            }
            .sortedByDescending { it.total }
            .take(6)

        DashboardUiState(
            nome = perfil.nome,
            salario = salario,
            totalGasto = totalGasto,
            saldo = saldo,
            percentualComprometido = percentualComprometido,
            totalInvestido = totalInvestido,
            totalGuardadoEmMetas = totalGuardadoEmMetas,
            porTipo = porTipo,
            topSubcategorias = topSubcategorias,
            temGastos = gastos.isNotEmpty()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardUiState()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DashboardViewModel(AppContainer.repositorio) }
        }
    }
}
