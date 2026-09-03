package com.example.myapplication

import com.example.myapplication.data.repository.InMemoryFinancasRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Testes do repositorio em memoria. Tudo e sincrono, entao basta ler o .value apos cada operacao.
class InMemoryFinancasRepositoryTest {

    @Test
    fun `adicionarGasto adiciona na lista e herda o tipo da subcategoria`() = runTest {
        val repo = InMemoryFinancasRepository()
        val subcategoria = repo.subcategorias.value.first()

        repo.adicionarGasto("Padaria", 25.0, subcategoria.id)

        val gastos = repo.gastos.value
        assertEquals(1, gastos.size)
        val gastoAdicionado = gastos.first()
        assertEquals(subcategoria.id, gastoAdicionado.subcategoriaId)
        assertEquals(subcategoria.tipo, gastoAdicionado.tipo)
    }

    @Test
    fun `adicionarSubcategoria cria com padrao falso e aparece na lista`() = runTest {
        val repo = InMemoryFinancasRepository()

        val nova = repo.adicionarSubcategoria("Assinaturas", com.example.myapplication.data.model.TipoGasto.VARIAVEL)

        assertFalse(nova.padrao)
        assertTrue(repo.subcategorias.value.any { it.id == nova.id })
    }

    @Test
    fun `removerSubcategoria remove tambem os gastos daquela subcategoria`() = runTest {
        val repo = InMemoryFinancasRepository()
        val nova = repo.adicionarSubcategoria("Assinaturas", com.example.myapplication.data.model.TipoGasto.VARIAVEL)
        repo.adicionarGasto("Streaming", 40.0, nova.id)

        repo.removerSubcategoria(nova.id)

        assertFalse(repo.subcategorias.value.any { it.id == nova.id })
        assertFalse(repo.gastos.value.any { it.subcategoriaId == nova.id })
    }

    @Test
    fun `depositarNaMeta soma no valorGuardado e cria movimentacao`() = runTest {
        val repo = InMemoryFinancasRepository()
        repo.adicionarMeta("Viagem", 1000.0)
        val meta = repo.metas.value.first()

        repo.depositarNaMeta(meta.id, 200.0)

        val metaAtualizada = repo.metas.value.first { it.id == meta.id }
        assertEquals(200.0, metaAtualizada.valorGuardado, 0.001)
        val movimentacoes = repo.depositos.value.filter { it.metaId == meta.id }
        assertEquals(1, movimentacoes.size)
        assertEquals(200.0, movimentacoes.first().valor, 0.001)
    }

    @Test
    fun `retirarDaMeta nunca deixa valorGuardado negativo`() = runTest {
        val repo = InMemoryFinancasRepository()
        repo.adicionarMeta("Viagem", 1000.0)
        val meta = repo.metas.value.first()
        repo.depositarNaMeta(meta.id, 50.0)

        repo.retirarDaMeta(meta.id, 500.0)

        val metaAtualizada = repo.metas.value.first { it.id == meta.id }
        assertEquals(0.0, metaAtualizada.valorGuardado, 0.001)
    }
}
