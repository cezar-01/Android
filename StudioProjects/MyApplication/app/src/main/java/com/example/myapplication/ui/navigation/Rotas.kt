package com.example.myapplication.ui.navigation

object Rotas {
    const val SALARIO = "salario"

    const val GASTOS = "gastos"

    const val DASHBOARD = "dashboard"

    const val METAS = "metas"

    const val PERFIL = "perfil"

    const val META_DETALHE = "meta_detalhe/{metaId}"

    const val ARG_META_ID = "metaId"

    fun metaDetalhe(metaId: String): String = "meta_detalhe/$metaId"
}
