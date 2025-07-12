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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.appcontact.db.AppDatabase
import com.example.appcontact.db.entity.Albums
import com.example.appcontact.db.entity.Songs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun AlbumsScreen(modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val showDialogUpdate = remember { mutableStateOf(false) }
    val showDialogDelete = remember { mutableStateOf(false) }

    val albumName = remember { mutableStateOf("") }
    val artistName = remember { mutableStateOf("")}
    val yearRealese = remember { mutableStateOf("")}
    val context = LocalContext.current

    val db = AppDatabase.getInstance(context)
    val dao = db.albumDao()

    val albumsList by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val albumToEdit = remember { mutableStateOf<Albums?>(null) }
    val albumToDelete = remember { mutableStateOf<Albums?>(null) }
    Box(
        // modifier dari parameter akan diterapkan ke Box sebagai container utama
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 20.dp),
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp) // Beri jarak antar kartu
        ) {
            items(albumsList) { album ->
                CardAlbum(
                    // Ambil data dari objek 'song'
                    judulAlbum = album.albumName ?: "Tanpa Judul",
                    artistName = album.artistName ?: "Tanpa Musisi",
                    yearRealese = album.tahunAlbum ?: "Tanpa Tahun",
                    onEditClick = {
                        // 1. Saat tombol edit diklik, simpan data lagu ke state
                        albumToEdit.value = album
                        // Isi state input dengan data yang ada
                        albumName.value = album.albumName ?: ""
                        artistName.value = album.artistName ?: ""
                        yearRealese.value = album.tahunAlbum ?: ""
                        // Tampilkan dialog
                        showDialogUpdate.value = true
                    },
                    onDeleteClick = {
                        // Saat tombol delete di kartu diklik
                        showDialogDelete.value = true
                        albumToDelete.value = album

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
        AddAlbumDialog(
            albumsName = albumName.value, // Data dummy untuk nama lagu
            artistName = artistName.value,
            yearRealese = yearRealese.value,// Data dummy untuk nama musisi
            onAlbumNameChange = {albumName.value = it},
            onArtistNameChange = {artistName.value = it},
            onYearRealese = {yearRealese.value =it},
            onDismissRequest = {showDialog.value = false}, // Kosongkan
            onSaveClick = {
                val newAlbum = Albums(
                    uid = System.currentTimeMillis().toInt(),
                    albumName = albumName.value,
                    artistName = artistName.value,
                    tahunAlbum = yearRealese.value
                )

                // 3. Luncurkan coroutine untuk menjalankan operasi insert di background thread
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertAll(newAlbum)
                    showDialog.value= false
                    albumName.value = ""
                    artistName.value = ""
                    yearRealese.value = ""
                    Log.d("DATABASE_INSERT", "Lagu baru berhasil disimpan: $newAlbum")
                }
            }
        )
    }

    if (showDialogUpdate.value){
        UpdateAlbumDialog(
            albumsName = albumName.value, // Data dummy untuk nama lagu
            artistName = artistName.value,
            yearRealese = yearRealese.value,// Data dummy untuk nama musisi
            onAlbumNameChange = {albumName.value = it},
            onArtistNameChange = {artistName.value = it},
            onYearRealese = {yearRealese.value = it},
            onDismissRequest = {showDialog.value = false}, // Kosongkan
            onSaveClick = {
                // 1. Ambil lagu yang akan di-edit dari state
                val albumInEdit = albumToEdit.value
                if (albumInEdit != null) {
                    // 2. Buat objek yang sudah di-update menggunakan .copy()
                    // Ini penting untuk menjaga `uid` tetap sama
                    val updatedSong = albumInEdit.copy(
                        albumName = albumName.value,
                        artistName = artistName.value,
                        tahunAlbum = yearRealese.value
                    )

                    // 3. Jalankan operasi update di background thread
                    scope.launch(Dispatchers.IO) {
                        dao.update(updatedSong)
                    }

                    // 4. Tutup dialog dan reset state
                    showDialogUpdate.value = false
                    albumName.value = ""
                    artistName.value = ""
                    yearRealese.value = ""
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
                albumToDelete.value?.let { albumUntukDihapus ->
                    scope.launch(Dispatchers.IO) {
                        // Hapus lagu tersebut
                        dao.delete(albumUntukDihapus)
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
fun ArtistAvatarAlbums(
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
fun CardAlbum(
    modifier: Modifier = Modifier,
    judulAlbum: String,
    artistName: String,
    yearRealese: String,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        // This parent Column will center everything inside it horizontally
        Column (
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // This centers all children
        ) {
            // Avatar
            ArtistAvatarAlbums(artistName = judulAlbum)

            Spacer(modifier = Modifier.height(8.dp)) // Use height for vertical spacing

            // Text for Song Title
            Text(
                text = judulAlbum,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            // Text for Musician
            Text(
                text = artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp)) // Use height for vertical spacing

            // Row for Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){

                Box(
                    modifier = Modifier.weight(1f)
                ){
                    Text("release " + yearRealese, fontStyle = FontStyle.Italic)
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Song"
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Song",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}





@Composable
fun AddAlbumDialog(
    albumsName: String,
    artistName: String,
    yearRealese: String,
    onAlbumNameChange: (String) -> Unit,
    onArtistNameChange: (String) -> Unit,
    onYearRealese: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Favorite Album") },
        text = {
            Column {
                OutlinedTextField(
                    value = albumsName,
                    onValueChange = { onAlbumNameChange(it) },
                    label = { Text("Album Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artistName,
                    onValueChange = { onArtistNameChange(it) },
                    label = { Text("Artist Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = yearRealese,
                    onValueChange = { onYearRealese(it) },
                    label = { Text("Year Release") })
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
fun UpdateAlbumDialog(
    albumsName: String,
    artistName: String,
    yearRealese: String,
    onAlbumNameChange: (String) -> Unit,
    onArtistNameChange: (String) -> Unit,
    onYearRealese: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Update Favorite Album") },
        text = {
            Column {
                OutlinedTextField(
                    value = albumsName,
                    onValueChange = { onAlbumNameChange(it) },
                    label = { Text("Album Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artistName,
                    onValueChange = { onArtistNameChange(it) },
                    label = { Text("Artist Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = yearRealese,
                    onValueChange = { onYearRealese(it) },
                    label = { Text("Year Release") }
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


