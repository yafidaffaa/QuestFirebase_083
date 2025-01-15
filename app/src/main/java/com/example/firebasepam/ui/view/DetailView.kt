import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.ui.viewmodel.DetailUiState
import com.example.firebasepam.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    nim: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onEditClick: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val detailUiState by viewModel.detailUiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(nim) {
        viewModel.getMahasiswaById(nim)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mahasiswa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            when (val state = detailUiState) {
                is DetailUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is DetailUiState.Success -> {
                    val mahasiswa = state.mahasiswa
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailMahasiswa(mahasiswa)

                        Spacer(modifier = Modifier.height(16.dp))

//                        Button(
//                            onClick = onEditClick,
//                            shape = MaterialTheme.shapes.small,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text(text = "Edit")
//                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Delete")
                        }
                    }
                }

                is DetailUiState.Error -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Gagal memuat data. Silakan coba lagi.")
                }
            }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Data") },
            text = { Text("Apakah Anda yakin ingin menghapus data ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMhs(nim)
                        showDeleteDialog = false
                        onDeleteSuccess()
                    }
                ) {
                    Text("Ya, Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Composable
fun DetailMahasiswa(
    mahasiswa: Mahasiswa
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Nama: ${mahasiswa.nama}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "NIM: ${mahasiswa.nim}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Angkatan: ${mahasiswa.angkatan}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Alamat: ${mahasiswa.alamat}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Jenis Kelamin: ${mahasiswa.jenis_kelamin}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Kelas: ${mahasiswa.kelas}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Judul Skripsi: ${mahasiswa.judul_skripsi}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Dosen Pembimbing 1: ${mahasiswa.dosen_pembimbing1}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Dosen Pembimbing 2: ${mahasiswa.dosen_pembimbing2}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

