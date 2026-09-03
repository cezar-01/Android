package com.example.myapplication

import com.example.myapplication.util.textoParaValor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Testes de textoParaValor. formatarMoeda nao e testado aqui pois depende do locale da JVM.
class FormatadoresTest {

    @Test
    fun `textoParaValor converte numero com milhar e virgula decimal`() {
        val resultado = textoParaValor("1.234,56")
        assertEquals(1234.56, resultado!!, 0.001)
    }

    @Test
    fun `textoParaValor converte numero so com virgula decimal`() {
        val resultado = textoParaValor("1234,56")
        assertEquals(1234.56, resultado!!, 0.001)
    }

    @Test
    fun `textoParaValor retorna nulo para texto vazio`() {
        assertNull(textoParaValor(""))
    }

    @Test
    fun `textoParaValor retorna nulo para texto invalido`() {
        assertNull(textoParaValor("abc"))
    }

    @Test
    fun `textoParaValor converte numero inteiro simples`() {
        val resultado = textoParaValor("10")
        assertEquals(10.0, resultado!!, 0.001)
    }
}
