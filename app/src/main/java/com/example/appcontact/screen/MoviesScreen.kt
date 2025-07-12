package com.example.appcontact.screen


import android.graphics.Movie
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.appcontact.db.AppDatabase
import com.example.appcontact.db.entity.Albums
import com.example.appcontact.db.entity.Movies
import com.example.appcontact.db.entity.Songs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun MoviesScreen(modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val showDialogUpdate = remember { mutableStateOf(false) }
    val showDialogDelete = remember { mutableStateOf(false) }
    val moviesName = remember { mutableStateOf("") }
    val directorName = remember { mutableStateOf("")}
    val context = LocalContext.current

    val db = AppDatabase.getInstance(context)
    val dao = db.movieDao()

    val moviesList by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val movieToEdit = remember { mutableStateOf<Movies?>(null) }
    val movieToDelete = remember { mutableStateOf<Movies?>(null) }
    Box(
        // modifier dari parameter akan diterapkan ke Box sebagai container utama
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 20.dp),
    ) {
        // 1. Konten utama layar Anda (bisa apa saja)
        // Diletakkan di tengah karena default contentAlignment Box adalah TopStart
        // Jika ingin di tengah, tambahkan contentAlignment = Alignment.Center pada Box
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp) // Beri jarak antar kartu
        ) {
            items(moviesList) { movie ->
                CardMovies(
                    judulMovie = movie.moviesName ?: "",
                    directorName = movie.directorName ?: "",
                    onEditClick = {
                        // 1. Saat tombol edit diklik, simpan data lagu ke state
                        movieToEdit.value = movie
                        // Isi state input dengan data yang ada
                        moviesName.value = movie.moviesName ?: ""
                        directorName.value = movie.directorName ?: ""
                        // Tampilkan dialog
                        showDialogUpdate.value = true
                    },
                    onDeleteClick = {
                        // Saat tombol delete di kartu diklik
                        showDialogDelete.value = true
                        movieToDelete.value = movie

                    }
                )
            }
        }

        // 2. Tambahkan FloatingActionButton sebagai child KEDUA di dalam Box
        FloatingActionButton(
            onClick = {   showDialog.value = true},
            // 3. Gunakan Modifier.align() untuk memposisikan FAB
            modifier = Modifier
                .align(Alignment.BottomEnd) // Posisi di kanan bawah
                .padding(16.dp) // Beri jarak dari tepi layar
        ) {
            Icon(Icons.Filled.Add, "Add")
        }
    }
    if (showDialog.value) {
        AddMoviesDialog(
            moviesName = moviesName.value, // Data dummy untuk nama lagu
            directorName = directorName.value,
           // Data dummy untuk nama musisi
            onMoviesNameChange = { moviesName.value = it},
            onDirecyortNameChange = {directorName.value = it},

            onDismissRequest = {showDialog.value = false}, // Kosongkan
            onSaveClick = {
                val newMovies = Movies(
                    uid = System.currentTimeMillis().toInt(),
                    moviesName = moviesName.value,
                    directorName = directorName.value
                )

                // 3. Luncurkan coroutine untuk menjalankan operasi insert di background thread
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertAll(newMovies)
                    showDialog.value= false
                    moviesName.value = ""
                    directorName.value = ""
                    Log.d("DATABASE_INSERT", "Lagu baru berhasil disimpan: $newMovies")
                }
            }
        )
    }

    if (showDialogUpdate.value){
        UpdateMoviesDialog(
            moviesName = moviesName.value, // Data dummy untuk nama lagu
            directorName = directorName.value,
            onMoviesNameChange = {moviesName.value = it},
            onDirecyortNameChange = {directorName.value = it},
            onDismissRequest = {showDialogUpdate.value = false},
            onSaveClick = {
                val movieInEdit = movieToEdit.value
                if (movieInEdit != null) {
                    // 2. Buat objek yang sudah di-update menggunakan .copy()
                    // Ini penting untuk menjaga `uid` tetap sama
                    val updatedMovie = movieInEdit.copy(
                        moviesName = moviesName.value,
                        directorName = directorName.value
                    )

                    // 3. Jalankan operasi update di background thread
                    scope.launch(Dispatchers.IO) {
                        dao.update(updatedMovie)
                    }

                    // 4. Tutup dialog dan reset state
                    showDialogUpdate.value = false
                    moviesName.value = ""
                    directorName.value = ""
                }
            }
        )
    }
    if (showDialogDelete.value){
        AlertDialogExample(
            onDismissRequest = {
                // Aksi saat dialog ditutup (misalnya, menekan tombol "Dismiss" atau di luar area dialog)
                showDialogDelete.value = false
            },
            onConfirmation = {
                movieToDelete.value?.let { movieUntukDihapus ->
                    scope.launch(Dispatchers.IO) {
                        // Hapus lagu tersebut
                        dao.delete(movieUntukDihapus)
                    }
                }
                // Tutup dialog setelah konfirmasi
                showDialogDelete.value = false
            },
            dialogTitle = "Warning !!!",
            dialogText = "are you sure delete this ?",
            icon = Icons.Default.Info // Ikon yang ingin ditampilkan
        )
    }
}
@Composable
fun ArtistAvatarMovies(
    artistName: String,
    modifier: Modifier = Modifier
) {
    // Ambil huruf pertama, pastikan tidak kosong
    val initial = artistName.take(1).uppercase()

    // Buat warna latar yang unik dan konsisten berdasarkan nama artis
    val backgroundColor = remember(artistName) {
        val hash = artistName.hashCode()
        Color(
            red = abs(hash * 43) % 256,
            green = abs(hash * 97) % 256,
            blue = abs(hash * 53) % 256
        )
    }

    Box(
        modifier = modifier
            .size(48.dp) // Ukuran avatar yang lebih pas
            .clip(CircleShape) // Membuat bentuknya menjadi bulat
            .background(backgroundColor),
        contentAlignment = Alignment.Center // Membuat teks berada di tengah
    ) {
        Text(
            text = initial,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // Warna teks agar kontras
        )
    }
}
@Composable
fun CardMovies(
    modifier: Modifier = Modifier,
    judulMovie: String,
    directorName: String,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Avatar di paling kiri

                ArtistAvatarMovies(artistName = judulMovie)




            Spacer(modifier = Modifier.width(16.dp))

            // Kolom untuk Teks
            Column(
                // 1. Gunakan weight(1f) agar kolom ini mengisi ruang & mendorong tombol ke kanan
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = judulMovie,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = directorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Spacer kecil sebelum tombol untuk estetika
            Spacer(modifier = Modifier.width(8.dp))

            // 2. Tambahkan IconButton untuk Edit
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Song"
                )
            }

            // 3. Tambahkan IconButton untuk Delete
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Song",
                    tint = MaterialTheme.colorScheme.error // Beri warna merah untuk aksi delete
                )
            }
        }
    }
}

@Composable
fun AddMoviesDialog(
    moviesName: String,
    directorName: String,

    onMoviesNameChange: (String) -> Unit,
    onDirecyortNameChange: (String) -> Unit,

    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Favorite Movies")},
        text = {
            Column {
                OutlinedTextField(
                    value = moviesName,
                    onValueChange = { onMoviesNameChange(it) },
                    label = { Text("Movies Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = directorName,
                    onValueChange = { onDirecyortNameChange(it) },
                    label = { Text("Director Name") }
                )

            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSaveClick() }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UpdateMoviesDialog(
    moviesName: String,
    directorName: String,

    onMoviesNameChange: (String) -> Unit,
    onDirecyortNameChange: (String) -> Unit,

    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Favorite Movies")},
        text = {
            Column {
                OutlinedTextField(
                    value = moviesName,
                    onValueChange = { onMoviesNameChange(it) },
                    label = { Text("Movies Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = directorName,
                    onValueChange = { onDirecyortNameChange(it) },
                    label = { Text("Director Name") }
                )

            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSaveClick() }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text("Cancel")
            }
        }
    )
}


