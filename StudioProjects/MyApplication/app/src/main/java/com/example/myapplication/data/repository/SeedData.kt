package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Subcategoria
import com.example.myapplication.data.model.TipoGasto

/** Subdivisoes que ja vem cadastradas no app. O usuario pode criar outras. */
object SeedData {

    fun subcategoriasPadrao(): List<Subcategoria> = listOf(
        // Fixos
        sub("fix_aluguel", "Aluguel", TipoGasto.FIXO),
        sub("fix_contas", "Contas de casa", TipoGasto.FIXO),
        sub("fix_internet", "Internet e telefone", TipoGasto.FIXO),
        sub("fix_transporte", "Transporte", TipoGasto.FIXO),
        sub("fix_educacao", "Educacao", TipoGasto.FIXO),
        // Variaveis
        sub("var_alimentacao", "Alimentacao", TipoGasto.VARIAVEL),
        sub("var_lazer", "Lazer", TipoGasto.VARIAVEL),
        sub("var_saude", "Saude", TipoGasto.VARIAVEL),
        sub("var_compras", "Compras", TipoGasto.VARIAVEL),
        sub("var_outros", "Outros", TipoGasto.VARIAVEL),
        // Investimentos
        sub("inv_poupanca", "Poupanca", TipoGasto.INVESTIMENTO),
        sub("inv_renda_fixa", "Renda fixa", TipoGasto.INVESTIMENTO),
        sub("inv_acoes", "Acoes", TipoGasto.INVESTIMENTO),
        sub("inv_reserva", "Reserva de emergencia", TipoGasto.INVESTIMENTO)
    )

    private fun sub(id: String, nome: String, tipo: TipoGasto) =
        Subcategoria(id = id, nome = nome, tipo = tipo, padrao = true)
}
