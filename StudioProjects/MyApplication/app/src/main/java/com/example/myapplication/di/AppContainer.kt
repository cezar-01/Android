package com.example.myapplication.di

import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.data.repository.InMemoryFinancasRepository

/**
 * Injecao de dependencia "na mao" (sem Hilt/Koin, para ficar simples de entender).
 *
 * Este e o unico lugar do app que decide QUAL implementacao de repositorio sera usada.
 * Para migrar para um banco externo depois, troque a linha de baixo por algo como:
 *
 *     val repositorio: FinancasRepository = RoomFinancasRepository(bancoDeDados.dao())
 */
object AppContainer {

    val repositorio: FinancasRepository by lazy { InMemoryFinancasRepository() }
}
