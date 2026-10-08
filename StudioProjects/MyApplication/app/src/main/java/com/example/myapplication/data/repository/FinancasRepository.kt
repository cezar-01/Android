package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Deposito
import com.example.myapplication.data.model.Gasto
import com.example.myapplication.data.model.Meta
import com.example.myapplication.data.model.PerfilFinanceiro
import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto
import kotlinx.coroutines.flow.StateFlow

interface FinancasRepository {

    val perfil: StateFlow<PerfilFinanceiro>
    val subcategorias: StateFlow<List<Subcategoria>>
    val gastos: StateFlow<List<Gasto>>
    val metas: StateFlow<List<Meta>>
    val depositos: StateFlow<List<Deposito>>

    suspend fun salvarPerfil(nome: String, salarioMensal: Double)

    suspend fun atualizarNome(nome: String)

    suspend fun atualizarFotoPerfil(uri: String?)

    suspend fun adicionarSubcategoria(nome: String, tipo: TipoGasto): Subcategoria
    suspend fun removerSubcategoria(id: String)

    suspend fun adicionarGasto(descricao: String, valor: Double, subcategoriaId: String)
    suspend fun removerGasto(id: String)

    suspend fun adicionarMeta(nome: String, valorAlvo: Double)
    suspend fun removerMeta(id: String)

    suspend fun depositarNaMeta(metaId: String, valor: Double)

    suspend fun retirarDaMeta(metaId: String, valor: Double)
}
