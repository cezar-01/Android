package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Deposito
import com.example.myapplication.data.model.Gasto
import com.example.myapplication.data.model.Meta
import com.example.myapplication.data.model.PerfilFinanceiro
import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryFinancasRepository : FinancasRepository {

    private val _perfil = MutableStateFlow(PerfilFinanceiro())
    override val perfil: StateFlow<PerfilFinanceiro> = _perfil.asStateFlow()

    private val _subcategorias = MutableStateFlow(SeedData.subcategoriasPadrao())
    override val subcategorias: StateFlow<List<Subcategoria>> = _subcategorias.asStateFlow()

    private val _gastos = MutableStateFlow<List<Gasto>>(emptyList())
    override val gastos: StateFlow<List<Gasto>> = _gastos.asStateFlow()

    private val _metas = MutableStateFlow<List<Meta>>(emptyList())
    override val metas: StateFlow<List<Meta>> = _metas.asStateFlow()

    private val _depositos = MutableStateFlow<List<Deposito>>(emptyList())
    override val depositos: StateFlow<List<Deposito>> = _depositos.asStateFlow()

    override suspend fun salvarPerfil(nome: String, salarioMensal: Double) {
        _perfil.update { it.copy(nome = nome.trim(), salarioMensal = salarioMensal) }
    }

    override suspend fun atualizarNome(nome: String) {
        _perfil.update { it.copy(nome = nome.trim()) }
    }

    override suspend fun atualizarFotoPerfil(uri: String?) {
        _perfil.update { it.copy(fotoUri = uri) }
    }

    override suspend fun adicionarSubcategoria(nome: String, tipo: TipoGasto): Subcategoria {
        val nova = Subcategoria(id = novoId(), nome = nome.trim(), tipo = tipo, padrao = false)
        _subcategorias.update { it + nova }
        return nova
    }

    override suspend fun removerSubcategoria(id: String) {
        _subcategorias.update { lista -> lista.filterNot { it.id == id } }
        _gastos.update { lista -> lista.filterNot { it.subcategoriaId == id } }
    }

    override suspend fun adicionarGasto(descricao: String, valor: Double, subcategoriaId: String) {
        val sub = _subcategorias.value.firstOrNull { it.id == subcategoriaId } ?: return
        val gasto = Gasto(
            id = novoId(),
            descricao = descricao.trim().ifBlank { sub.nome },
            valor = valor,
            subcategoriaId = sub.id,
            tipo = sub.tipo,
            dataMillis = System.currentTimeMillis()
        )
        _gastos.update { it + gasto }
    }

    override suspend fun removerGasto(id: String) {
        _gastos.update { lista -> lista.filterNot { it.id == id } }
    }

    override suspend fun adicionarMeta(nome: String, valorAlvo: Double) {
        val meta = Meta(
            id = novoId(),
            nome = nome.trim(),
            valorAlvo = valorAlvo,
            valorGuardado = 0.0,
            criadaEmMillis = System.currentTimeMillis()
        )
        _metas.update { it + meta }
    }

    override suspend fun removerMeta(id: String) {
        _metas.update { lista -> lista.filterNot { it.id == id } }
        _depositos.update { lista -> lista.filterNot { it.metaId == id } }
    }

    override suspend fun depositarNaMeta(metaId: String, valor: Double) {
        if (valor <= 0.0) return
        movimentar(metaId, valor)
    }

    override suspend fun retirarDaMeta(metaId: String, valor: Double) {
        if (valor <= 0.0) return
        val meta = _metas.value.firstOrNull { it.id == metaId } ?: return
        val retirada = valor.coerceAtMost(meta.valorGuardado)
        if (retirada <= 0.0) return
        movimentar(metaId, -retirada)
    }

    private fun movimentar(metaId: String, valor: Double) {
        _metas.update { lista ->
            lista.map { meta ->
                if (meta.id == metaId) {
                    meta.copy(valorGuardado = (meta.valorGuardado + valor).coerceAtLeast(0.0))
                } else {
                    meta
                }
            }
        }
        _depositos.update {
            it + Deposito(
                id = novoId(),
                metaId = metaId,
                valor = valor,
                dataMillis = System.currentTimeMillis()
            )
        }
    }

    private fun novoId(): String = UUID.randomUUID().toString()
}
