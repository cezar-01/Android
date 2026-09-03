package com.example.myapplication

import com.example.myapplication.data.model.Meta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Testes da data class Meta (calculos derivados: valorRestante, progresso, concluida)
class ModelosTest {

    @Test
    fun `valorRestante nunca fica negativo quando guardado passa do alvo`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 150.0,
            criadaEmMillis = 0L
        )
        assertEquals(0.0, meta.valorRestante, 0.001)
    }

    @Test
    fun `valorRestante calcula a diferenca quando ainda falta guardar`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 30.0,
            criadaEmMillis = 0L
        )
        assertEquals(70.0, meta.valorRestante, 0.001)
    }

    @Test
    fun `progresso fica entre 0 e 1 mesmo passando do alvo`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 300.0,
            criadaEmMillis = 0L
        )
        assertTrue(meta.progresso in 0f..1f)
        assertEquals(1f, meta.progresso, 0.001f)
    }

    @Test
    fun `progresso e zero quando valorAlvo e zero`() {
        val meta = Meta(
            id = "1",
            nome = "Sem alvo",
            valorAlvo = 0.0,
            valorGuardado = 50.0,
            criadaEmMillis = 0L
        )
        assertEquals(0f, meta.progresso, 0.001f)
    }

    @Test
    fun `concluida e true quando guardado e igual ao alvo`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 100.0,
            criadaEmMillis = 0L
        )
        assertTrue(meta.concluida)
    }

    @Test
    fun `concluida e true quando guardado passa do alvo`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 120.0,
            criadaEmMillis = 0L
        )
        assertTrue(meta.concluida)
    }

    @Test
    fun `concluida e false quando ainda falta guardar`() {
        val meta = Meta(
            id = "1",
            nome = "Viagem",
            valorAlvo = 100.0,
            valorGuardado = 99.0,
            criadaEmMillis = 0L
        )
        assertFalse(meta.concluida)
    }
}
