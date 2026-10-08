package com.example.myapplication.data.model

enum class TipoGasto(val rotulo: String) {
    FIXO("Fixos"),
    VARIAVEL("Variaveis"),
    INVESTIMENTO("Investimentos");

    companion object {
        fun porId(id: String): TipoGasto = values().first { it.name == id }
    }
}

data class Subcategoria(
    val id: String,
    val nome: String,
    val tipo: TipoGasto,
    val padrao: Boolean = false
)

data class Gasto(
    val id: String,
    val descricao: String,
    val valor: Double,
    val subcategoriaId: String,
    val tipo: TipoGasto,
    val dataMillis: Long
)

data class Meta(
    val id: String,
    val nome: String,
    val valorAlvo: Double,
    val valorGuardado: Double,
    val criadaEmMillis: Long
) {
    val valorRestante: Double get() = (valorAlvo - valorGuardado).coerceAtLeast(0.0)

    val progresso: Float
        get() = if (valorAlvo <= 0.0) 0f else (valorGuardado / valorAlvo).toFloat().coerceIn(0f, 1f)

    val concluida: Boolean get() = valorGuardado >= valorAlvo && valorAlvo > 0.0
}

data class Deposito(
    val id: String,
    val metaId: String,
    val valor: Double,
    val dataMillis: Long
)

data class PerfilFinanceiro(
    val nome: String = "",
    val salarioMensal: Double = 0.0,
    val fotoUri: String? = null
) {
    val configurado: Boolean get() = salarioMensal > 0.0
}
