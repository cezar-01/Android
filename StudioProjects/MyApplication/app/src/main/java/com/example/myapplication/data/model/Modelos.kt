package com.example.myapplication.data.model

/**
 * Modelos de dominio do app.
 *
 * Sao data classes puras, sem nenhuma dependencia de Android ou de banco de dados.
 * Quando o app for integrado a um banco externo (Room, Firebase, API REST...),
 * basta criar entidades/DTOs proprios e mapea-los para estas classes.
 */

/** As tres grandes divisoes de gastos pedidas no projeto. */
enum class TipoGasto(val rotulo: String) {
    FIXO("Fixos"),
    VARIAVEL("Variaveis"),
    INVESTIMENTO("Investimentos");

    companion object {
        fun porId(id: String): TipoGasto = values().first { it.name == id }
    }
}

/**
 * Subdivisao de um tipo de gasto (ex.: Aluguel dentro de FIXO, Lazer dentro de VARIAVEL,
 * Poupanca dentro de INVESTIMENTO). O usuario pode cadastrar novas.
 */
data class Subcategoria(
    val id: String,
    val nome: String,
    val tipo: TipoGasto,
    /** true = veio da lista inicial do app; false = criada pelo usuario (pode ser excluida). */
    val padrao: Boolean = false
)

/** Um lancamento de gasto. */
data class Gasto(
    val id: String,
    val descricao: String,
    val valor: Double,
    val subcategoriaId: String,
    val tipo: TipoGasto,
    val dataMillis: Long
)

/** Meta de economia ("cofrinho"/"caixinha"), ex.: viagem de R$ 10.000. */
data class Meta(
    val id: String,
    val nome: String,
    val valorAlvo: Double,
    val valorGuardado: Double,
    val criadaEmMillis: Long
) {
    /** Quanto ainda falta guardar (nunca negativo). */
    val valorRestante: Double get() = (valorAlvo - valorGuardado).coerceAtLeast(0.0)

    /** Progresso de 0f a 1f, pronto para barras de progresso. */
    val progresso: Float
        get() = if (valorAlvo <= 0.0) 0f else (valorGuardado / valorAlvo).toFloat().coerceIn(0f, 1f)

    val concluida: Boolean get() = valorGuardado >= valorAlvo && valorAlvo > 0.0
}

/** Movimentacao dentro de uma meta. Valor positivo = deposito, negativo = retirada. */
data class Deposito(
    val id: String,
    val metaId: String,
    val valor: Double,
    val dataMillis: Long
)

/** Dados que o usuario informa na primeira tela. */
data class PerfilFinanceiro(
    val nome: String = "",
    val salarioMensal: Double = 0.0,
    /** Uri da foto escolhida pelo usuario (content://...). Nula = usa o avatar padrao. */
    val fotoUri: String? = null
) {
    val configurado: Boolean get() = salarioMensal > 0.0
}
