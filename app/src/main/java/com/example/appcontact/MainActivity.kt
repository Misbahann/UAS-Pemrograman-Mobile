package com.example.appcontact

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appcontact.ui.theme.AppContactTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppContactTheme {
                // 1. Buat NavController di sini
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // 2. Gunakan slot 'topBar' untuk meletakkan TabRow
                    topBar = {
                        Column {
                            TopAppBar(
                                colors = topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("My Favorite")
                                }
                            )
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination


                            PrimaryTabRow(

                                // Tentukan tab yang terpilih berdasarkan rute navigasi
                                selectedTabIndex = Destination.entries.indexOfFirst {
                                    it.route == currentDestination?.route
                                }.takeIf { it != -1 } ?: 0,
//                                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                            )

                            {

                                // Buat Tab untuk setiap destinasi
                                Destination.entries.forEach { destination ->
                                    Tab(
                                        selected = currentDestination?.hierarchy?.any {
                                            it.route == destination.route
                                        } == true,
                                        onClick = {
                                            // Navigasi saat tab diklik
                                            navController.navigate(destination.route) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select tabs
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // re-selecting the same item
                                                launchSingleTop = true
                                                // Restore state when re-selecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        text = { Text(destination.label) }
                                    )
                                }
                            }
                        }

                        // Ambil rute saat ini untuk menentukan tab mana yang aktif

                    }
                ) { innerPadding ->
                    // 3. Panggil AppNavHost sebagai konten utama
                    // Berikan padding dari Scaffold agar konten tidak tertutup topBar
                    AppNavHost(
                        navController = navController,
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}