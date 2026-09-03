# Calculadora de Gastos

Aplicativo Android para organização financeira pessoal, desenvolvido em Kotlin com
Jetpack Compose. Trabalho da disciplina de Desenvolvimento Mobile.

## O problema

Muitas pessoas não sabem para onde vai o próprio salário no fim do mês: gastos fixos,
gastos variáveis e investimentos ficam misturados na cabeça (ou em planilhas soltas),
sem uma visão clara de quanto já foi comprometido e quanto sobra. O app propõe um fluxo
simples — cadastrar o salário, lançar os gastos já classificados por tipo, acompanhar
tudo em um dashboard visual e guardar dinheiro para objetivos concretos (metas) — para
que o usuário enxergue rapidamente sua situação financeira e tome decisões melhores.

## As 5 telas

1. **Salário** (`ui/salario`) — tela de entrada/onboarding. O usuário informa nome e
   salário mensal, que servem de base para todos os cálculos de percentual do app.
   Só aparece uma vez, antes do perfil estar configurado.
2. **Gastos** (`ui/gastos`) — cadastro e listagem dos gastos, agrupados pelas 3
   divisões (Fixos, Variáveis, Investimentos). Permite adicionar um gasto novo, remover
   gastos existentes e criar novas subdivisões dentro de cada divisão.
3. **Dashboard** (`ui/dashboard`) — visão geral do mês: salário x total gasto x saldo,
   percentual do salário já comprometido, gráfico de pizza por divisão, gráfico de
   barras das maiores subdivisões e total guardado em metas.
4. **Metas** (`ui/metas`) — lista de objetivos financeiros ("cofrinhos"), cada um com
   valor alvo, valor já guardado e barra de progresso. Permite criar, remover e guardar
   dinheiro rapidamente em uma meta.
5. **Detalhe da meta** (`ui/metas`) — tela de uma meta específica, com anel de
   progresso, resumo de valores (guardado / falta / alvo), botão para guardar ou
   retirar dinheiro e histórico de movimentações.

## As 3 divisões de gastos

Todo gasto pertence a uma das três divisões abaixo (`TipoGasto`). Cada divisão tem
subcategorias padrão para facilitar o começo, mas o usuário pode criar quantas
subdivisões quiser dentro de qualquer uma delas.

| Divisão | Exemplos de subdivisões padrão |
|---|---|
| **Fixos** | Aluguel, Contas de casa, Internet e telefone, Transporte, Educação |
| **Variáveis** | Alimentação, Lazer, Saúde, Compras, Outros |
| **Investimentos** | Poupança, Renda fixa, Ações, Reserva de emergência |

Novas subdivisões criadas pelo usuário (ex.: "Academia" dentro de Variáveis, ou
"Cripto" dentro de Investimentos) funcionam exatamente como as padrão em todas as
telas e gráficos.

## Arquitetura

- **UI declarativa**: Jetpack Compose + Material 3 em todas as telas.
- **MVVM**: cada tela tem um `ViewModel` próprio que expõe estado imutável via
  `StateFlow` (`UiState`), consumido pela tela com `collectAsState()`. A tela nunca
  manipula dados diretamente, só chama funções do ViewModel.
- **Repository Pattern**: toda a camada de dados fica atrás da interface
  `FinancasRepository`. ViewModels dependem apenas dessa interface, nunca de uma
  implementação concreta.
- **Injeção manual**: não há Hilt/Koin. Um único objeto, `AppContainer`, guarda a
  instância do repositório usada pelo app inteiro; os `Factory` de cada ViewModel
  buscam a dependência ali.
- **Gráficos sem biblioteca externa**: o donut (`GraficoPizza`) e as barras
  (`GraficoBarras`) são desenhados à mão com `Canvas` do Compose (`drawArc`,
  `drawRoundRect`, etc.), sem nenhuma lib de charts.

## Estrutura de pastas

Pacote base: `com.example.myapplication`

```
com/example/myapplication/
├── data/
│   ├── model/         # classes de domínio (Gasto, Meta, Subcategoria, etc.)
│   └── repository/    # interface FinancasRepository e sua implementação em memória
├── di/                # AppContainer: ponto único de injeção manual de dependências
├── ui/
│   ├── theme/         # cores, tipografia e o tema Material 3 do app
│   ├── navigation/     # rotas, NavHost e a barra de navegação inferior
│   ├── components/    # peças reutilizáveis (cartão, campo de dinheiro, barra de progresso...)
│   ├── charts/        # gráficos de pizza e de barras desenhados com Canvas
│   ├── salario/       # tela 1: cadastro de nome e salário
│   ├── gastos/        # tela 2: lançamento e listagem de gastos por divisão
│   ├── dashboard/      # tela 3: resumo visual do mês
│   └── metas/         # telas 4 e 5: lista de metas e detalhe de uma meta
└── util/               # funções utilitárias (formatação de moeda, data, percentual)
```

## Como plugar um banco de dados externo

Hoje os dados vivem apenas em memória (`InMemoryFinancasRepository`), mas o app foi
desenhado para trocar de fonte de dados sem tocar em nenhuma tela ou ViewModel — tudo
depende da interface `FinancasRepository`, nunca da implementação concreta.

Passo a passo para plugar, por exemplo, Room ou Firebase:

1. **Criar uma nova classe que implemente `FinancasRepository`**, por exemplo
   `RoomFinancasRepository` ou `FirebaseFinancasRepository`, implementando os mesmos
   `StateFlow` e as mesmas funções `suspend` da interface.
2. **Mapear as entidades do banco para os models de domínio** (`Gasto`, `Meta`,
   `Subcategoria`, etc.), convertendo o retorno do banco (entidades Room, documentos
   Firestore) para as classes que o resto do app já conhece.
3. **Trocar uma linha em `di/AppContainer.kt`**, apontando `repositorio` para a nova
   implementação em vez de `InMemoryFinancasRepository`.

Nenhuma tela e nenhum ViewModel precisa mudar, porque todos eles só conhecem a
interface `FinancasRepository` — a origem real dos dados é um detalhe escondido atrás
dela.

Exemplo ilustrativo com Room (entidade + DAO devolvendo `Flow`):

```kotlin
@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey val id: String,
    val descricao: String,
    val valor: Double,
    val subcategoriaId: String,
    val tipo: String,
    val dataMillis: Long
)

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos ORDER BY dataMillis DESC")
    fun observarGastos(): Flow<List<GastoEntity>>

    @Insert
    suspend fun inserir(gasto: GastoEntity)

    @Delete
    suspend fun remover(gasto: GastoEntity)
}
```

Dentro de `RoomFinancasRepository`, o `Flow<List<GastoEntity>>` do DAO seria mapeado
(`map { lista -> lista.map { it.paraModeloDominio() } }`) e exposto como
`StateFlow<List<Gasto>>`, respeitando exatamente o contrato de `FinancasRepository`.

## Limitação atual conhecida

A implementação ativa hoje, `InMemoryFinancasRepository`, guarda tudo apenas na
memória do processo. Isso significa que **todos os dados são perdidos ao fechar o
app** (ou quando o sistema mata o processo em segundo plano). Persistência real exige
implementar uma das alternativas descritas na seção anterior.

## Como rodar

1. Abrir a pasta do projeto no Android Studio.
2. Aguardar a sincronização do Gradle (Sync Project with Gradle Files).
3. Selecionar um emulador com API 24 ou superior (ou um dispositivo físico compatível).
4. Rodar o app (Run 'app').
