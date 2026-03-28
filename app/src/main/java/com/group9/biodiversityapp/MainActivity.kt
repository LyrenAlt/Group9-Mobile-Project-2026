package com.group9.biodiversityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.group9.biodiversityapp.ui.navigation.NavRoutes
import com.group9.biodiversityapp.ui.screens.BrowseScreen
import com.group9.biodiversityapp.ui.screens.DetailScreen
import com.group9.biodiversityapp.ui.screens.SearchScreen
import com.group9.biodiversityapp.ui.theme.BiodiversityAppTheme
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiodiversityAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.SEARCH
                    ) {
                        // Landing page: search bar + group grid
                        composable(NavRoutes.SEARCH) {
                            SearchScreen(
                                onNavigateToDetail = { taxonId ->
                                    navController.navigate(NavRoutes.detail(taxonId))
                                },
                                onNavigateToBrowse = { groupId, groupName ->
                                    val encoded = URLEncoder.encode(groupName, "UTF-8")
                                    navController.navigate(NavRoutes.browseWithGroup(groupId, encoded))
                                },
                                onNavigateToBrowseAll = {
                                    navController.navigate(NavRoutes.BROWSE)
                                }
                            )
                        }

                        // Browse all species (no group filter)
                        composable(NavRoutes.BROWSE) {
                            BrowseScreen(
                                onNavigateToDetail = { taxonId ->
                                    navController.navigate(NavRoutes.detail(taxonId))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Browse species by group
                        composable(
                            route = NavRoutes.BROWSE_WITH_GROUP,
                            arguments = listOf(
                                navArgument("groupId") { type = NavType.StringType },
                                navArgument("groupName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId")
                            val groupName = backStackEntry.arguments?.getString("groupName")?.let {
                                URLDecoder.decode(it, "UTF-8")
                            }
                            BrowseScreen(
                                groupId = groupId,
                                groupName = groupName,
                                onNavigateToDetail = { taxonId ->
                                    navController.navigate(NavRoutes.detail(taxonId))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Species detail
                        composable(
                            route = NavRoutes.DETAIL,
                            arguments = listOf(
                                navArgument("taxonId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val taxonId = backStackEntry.arguments?.getString("taxonId") ?: return@composable
                            DetailScreen(
                                taxonId = taxonId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
