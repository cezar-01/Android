package com.example.myapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Modelo simples de item da barra inferior
private data class ItemNavegacao(val rota: String, val rotulo: String, val icone: ImageVector)

// Lista fixa com as 4 telas principais do app
private val itensNavegacao = listOf(
    ItemNavegacao(Rotas.GASTOS, "Gastos", Icons.Default.Receipt),
    ItemNavegacao(Rotas.DASHBOARD, "Dashboard", Icons.Default.PieChart),
    ItemNavegacao(Rotas.METAS, "Metas", Icons.Default.Savings),
    ItemNavegacao(Rotas.PERFIL, "Perfil", Icons.Default.Person)
)

// Barra inferior stateless: apenas le e navega usando o navController recebido
@Composable
fun BarraInferior(navController: NavHostController) {
    val destinoAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = destinoAtual?.destination?.route

    NavigationBar {
        itensNavegacao.forEach { item ->
            NavigationBarItem(
                selected = rotaAtual == item.rota,
                onClick = {
                    navController.navigate(item.rota) {
                        // Evita empilhar varias copias da mesma tela e preserva o estado
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icone, contentDescription = item.rotulo) },
                label = { Text(item.rotulo) }
            )
        }
    }
}
