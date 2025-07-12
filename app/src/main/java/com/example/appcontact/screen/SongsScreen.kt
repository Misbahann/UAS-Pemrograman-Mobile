package com.example.appcontact.screen

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.appcontact.db.AppDatabase
import com.example.appcontact.db.entity.Songs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun SongScreen(modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val showDialogUpdate = remember { mutableStateOf(false) }
    val showDialogDelete = remember { mutableStateOf(false) }
    val songName = remember { mutableStateOf("") }
    val musisiName = remember { mutableStateOf("")}
    val context = LocalContext.current

    val db = AppDatabase.getInstance(context)
    val dao = db.songDao()

    val songsList by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val songToEdit = remember { mutableStateOf<Songs?>(null) }
    val songToDelete = remember { mutableStateOf<Songs?>(null) }
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
            items(songsList) { song ->
                CardSong(
                    // Ambil data dari objek 'song'
                    judulLagu = song.songsName ?: "Tanpa Judul",
                    musisi = song.musisiuName ?: "Tanpa Musisi",
                    onEditClick = {
                        // 1. Saat tombol edit diklik, simpan data lagu ke state
                        songToEdit.value = song
                        // Isi state input dengan data yang ada
                        songName.value = song.songsName ?: ""
                        musisiName.value = song.musisiuName ?: ""
                        // Tampilkan dialog
                        showDialogUpdate.value = true
                    },
                    onDeleteClick = {
                        showDialogDelete.value = true
                        songToDelete.value = song
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
        AddSongDialog(
            songName = songName.value, // Data dummy untuk nama lagu
            artistName = musisiName.value, // Data dummy untuk nama musisi
            onSongNameChange = { songName.value = it },
            onArtistNameChange = { musisiName.value = it },
            onDismissRequest = {showDialog.value = false
                               songName.value = ""
                               musisiName.value = ""}, // Kosongkan
            onSaveClick = {
                val newSong = Songs(
                    uid = System.currentTimeMillis().toInt(),
                    songsName = songName.value,
                    musisiuName = musisiName.value
                )

                // 3. Luncurkan coroutine untuk menjalankan operasi insert di background thread
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertAll(newSong)
                    showDialog.value= false
                    songName.value = ""
                    musisiName.value = ""
                    Log.d("DATABASE_INSERT", "Lagu baru berhasil disimpan: $newSong")
                }
            } // Kosongkan
        )
    }

    if (showDialogUpdate.value){
        UpdateSongDialog(
            songName = songName.value, // Data dummy untuk nama lagu
            artistName = musisiName.value, // Data dummy untuk nama musisi
            onSongNameChange = { songName.value = it },
            onArtistNameChange = { musisiName.value = it },
            onDismissRequest = {showDialogUpdate.value = false
                songName.value = ""
                musisiName.value = ""}, // Kosongkan
            onSaveClick = {
                // 1. Ambil lagu yang akan di-edit dari state
                val songInEdit = songToEdit.value
                if (songInEdit != null) {
                    // 2. Buat objek yang sudah di-update menggunakan .copy()
                    // Ini penting untuk menjaga `uid` tetap sama
                    val updatedSong = songInEdit.copy(
                        songsName = songName.value,
                        musisiuName = musisiName.value
                    )

                    // 3. Jalankan operasi update di background thread
                    scope.launch(Dispatchers.IO) {
                        dao.update(updatedSong)
                    }

                    // 4. Tutup dialog dan reset state
                    showDialogUpdate.value = false
                    songName.value = ""
                    musisiName.value = ""
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
                songToDelete.value?.let { songUntukDihapus ->
                    scope.launch(Dispatchers.IO) {
                        // Hapus lagu tersebut
                        dao.delete(songUntukDihapus)
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
fun ArtistAvatar(
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
fun CardSong(
    modifier: Modifier = Modifier,
    judulLagu: String,
    musisi: String,
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
            ArtistAvatar(artistName = judulLagu)

            Spacer(modifier = Modifier.width(16.dp))

            // Kolom untuk Teks
            Column(
                // 1. Gunakan weight(1f) agar kolom ini mengisi ruang & mendorong tombol ke kanan
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = judulLagu,
                    style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = musisi,
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
fun AddSongDialog(
    songName: String,
    artistName: String,
    onSongNameChange: (String) -> Unit,
    onArtistNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Favorite Song") },
        text = {
            Column {
                OutlinedTextField(
                    value = songName,
                    onValueChange = { onSongNameChange(it) },
                    label = { Text("Song Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artistName,
                    onValueChange = { onArtistNameChange(it) },
                    label = { Text("Artist Name") }
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
fun UpdateSongDialog(
    songName: String,
    artistName: String,
    onSongNameChange: (String) -> Unit,
    onArtistNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Update Song") },
        text = {
            Column {
                OutlinedTextField(
                    value = songName,
                    onValueChange = { onSongNameChange(it) },
                    label = { Text("Song Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artistName,
                    onValueChange = { onArtistNameChange(it) },
                    label = { Text("Artist Name") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSaveClick() }
            ) {
                Text("Update")
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
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


