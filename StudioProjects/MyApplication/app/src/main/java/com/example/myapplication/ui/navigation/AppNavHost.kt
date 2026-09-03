package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.dashboard.DashboardScreen
import com.example.myapplication.ui.gastos.GastosScreen
import com.example.myapplication.ui.metas.MetaDetalheScreen
import com.example.myapplication.ui.metas.MetasScreen
import com.example.myapplication.ui.salario.SalarioScreen

// Ponto central de navegacao do app: decide quais telas existem e quando a barra
// inferior deve aparecer.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // Observa a rota atual para saber se a barra inferior deve ser exibida.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route
    val mostrarBarraInferior = rotaAtual == Rotas.GASTOS ||
        rotaAtual == Rotas.DASHBOARD ||
        rotaAtual == Rotas.METAS

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraInferior(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = Rotas.SALARIO) {
                composable(Rotas.SALARIO) {
                    SalarioScreen(
                        aoConcluir = {
                            navController.navigate(Rotas.GASTOS) {
                                popUpTo(Rotas.SALARIO) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Rotas.GASTOS) {
                    GastosScreen()
                }
                composable(Rotas.DASHBOARD) {
                    DashboardScreen()
                }
                composable(Rotas.METAS) {
                    MetasScreen(
                        aoAbrirMeta = { id -> navController.navigate(Rotas.metaDetalhe(id)) }
                    )
                }
                composable(
                    route = Rotas.META_DETALHE,
                    arguments = listOf(navArgument(Rotas.ARG_META_ID) { type = NavType.StringType })
                ) { entry ->
                    val metaId = entry.arguments?.getString(Rotas.ARG_META_ID).orEmpty()
                    MetaDetalheScreen(
                        metaId = metaId,
                        aoVoltar = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
