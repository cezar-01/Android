package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Deposito
import com.example.myapplication.data.model.Gasto
import com.example.myapplication.data.model.Meta
import com.example.myapplication.data.model.PerfilFinanceiro
import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato unico de acesso a dados do app.
 *
 * PONTO DE INTEGRACAO FUTURA COM BANCO EXTERNO:
 * hoje existe apenas [InMemoryFinancasRepository]. Para plugar Room, Firebase ou uma API,
 * crie uma nova classe que implemente esta interface (ex.: RoomFinancasRepository) e troque
 * a instancia criada em [com.example.myapplication.di.AppContainer]. Nenhuma tela ou
 * ViewModel precisa ser alterada, porque todos dependem so desta interface.
 *
 * As leituras sao StateFlow para que a UI observe as mudancas; as escritas sao suspend
 * para ja estarem prontas para I/O real (rede/disco).
 */
interface FinancasRepository {

    val perfil: StateFlow<PerfilFinanceiro>
    val subcategorias: StateFlow<List<Subcategoria>>
    val gastos: StateFlow<List<Gasto>>
    val metas: StateFlow<List<Meta>>
    val depositos: StateFlow<List<Deposito>>

    suspend fun salvarPerfil(nome: String, salarioMensal: Double)

    /** Troca so o nome, sem mexer no salario nem na foto (usado na aba Perfil). */
    suspend fun atualizarNome(nome: String)

    /** Define ou remove (null) a foto de perfil. */
    suspend fun atualizarFotoPerfil(uri: String?)

    suspend fun adicionarSubcategoria(nome: String, tipo: TipoGasto): Subcategoria
    suspend fun removerSubcategoria(id: String)

    suspend fun adicionarGasto(descricao: String, valor: Double, subcategoriaId: String)
    suspend fun removerGasto(id: String)

    suspend fun adicionarMeta(nome: String, valorAlvo: Double)
    suspend fun removerMeta(id: String)

    /** Guarda dinheiro no cofrinho da meta. */
    suspend fun depositarNaMeta(metaId: String, valor: Double)

    /** Tira dinheiro do cofrinho (nunca deixa o guardado ficar negativo). */
    suspend fun retirarDaMeta(metaId: String, valor: Double)
}
