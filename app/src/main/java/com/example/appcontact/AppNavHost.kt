package com.example.appcontact


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appcontact.screen.AlbumsScreen
import com.example.appcontact.screen.MoviesScreen
import com.example.appcontact.screen.SongScreen

// 1. Ubah parameter: dari 'modifier' menjadi 'contentPadding'
@Composable
fun AppNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Destination.SONGS.route,
        // 2. Hapus modifier dari NavHost agar ia mengisi seluruh ruang Scaffold
    ) {
        composable(route = Destination.SONGS.route) {
            // 3. Teruskan contentPadding ke setiap layar
            SongScreen(
                modifier = Modifier.padding(contentPadding)
            )
        }
        composable(route = Destination.PHOTOS.route) {
//            ScreenContent(
//                text = "Layar Foto (Photos)",
//                modifier = Modifier.padding(contentPadding) // Beri padding juga
//            )
            AlbumsScreen(
                modifier = Modifier.padding(contentPadding)
            )
        }
        composable(route = Destination.MOVIES.route) {
//            ScreenContent(
//                text = "Layar Film (Movies)",
//                modifier = Modifier.padding(contentPadding) // Beri padding juga
//            )

            MoviesScreen(
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

// Tidak perlu diubah, karena sudah menerima modifier
@Composable
fun ScreenContent(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()

    ) {
        Text(text = text)
        ExtendedExample(
            // BENAR: Berikan sebuah blok lambda
            onClick = { println("ikan") }
        )
    }
}

@Composable
fun ExtendedExample(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
        text = { Text(text = "Extended FAB") },
    )
}