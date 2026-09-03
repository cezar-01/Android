package com.example.myapplication.ui.navigation

// Centraliza os nomes das rotas de navegacao do app, evitando strings soltas espalhadas pelas telas.
object Rotas {
    // Tela inicial de cadastro do salario/nome
    const val SALARIO = "salario"

    // Tela de lancamento e listagem de gastos
    const val GASTOS = "gastos"

    // Tela com o resumo financeiro e graficos
    const val DASHBOARD = "dashboard"

    // Tela com a lista de metas (cofrinho)
    const val METAS = "metas"

    // Tela de detalhe de uma meta especifica, recebe o id da meta como argumento
    const val META_DETALHE = "meta_detalhe/{metaId}"

    // Nome do argumento de navegacao usado na rota META_DETALHE
    const val ARG_META_ID = "metaId"

    // Monta a rota concreta de detalhe substituindo o placeholder pelo id real da meta
    fun metaDetalhe(metaId: String): String = "meta_detalhe/$metaId"
}
