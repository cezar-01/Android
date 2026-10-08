package com.example.myapplication.di

import com.example.myapplication.data.repository.FinancasRepository
import com.example.myapplication.data.repository.InMemoryFinancasRepository

object AppContainer {

    val repositorio: FinancasRepository by lazy { InMemoryFinancasRepository() }
}
