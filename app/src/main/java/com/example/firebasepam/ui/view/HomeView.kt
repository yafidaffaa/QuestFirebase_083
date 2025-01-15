package com.example.firebasepam.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.ui.viewmodel.HomeUiState
import com.example.firebasepam.ui.viewmodel.HomeViewModel
import com.example.firebasepam.ui.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Gray),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mahasiswa")
            }
        },
    ) { innerPadding ->
        HomeStatus(
            homeUiState = viewModel.mhsUIState,
            retryAction = { viewModel.getMhs() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = {
                viewModel.deleteMhs(it)
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    var deleteConfirm by rememberSaveable { mutableStateOf<Mahasiswa?>(null) }
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())
        is HomeUiState.Success ->

            MhsLayout(
                mahasiswa = homeUiState.mahasiswa, modifier = modifier.fillMaxWidth(),
                onDetailClick = onDetailClick,
                onDeleteClick = {
                    deleteConfirm = it
                }
            )

        is HomeUiState.Error -> OnError(
            message = homeUiState.message.message ?: "Error",
            retryAction = retryAction,
            modifier = modifier.fillMaxSize()
        )
    }
    deleteConfirm?.let { data ->
        DeleteConfirmationDialog(
            onDeleteConfirm = {
                onDeleteClick(data)
                deleteConfirm = null
            },
            onDeleteCancel = { deleteConfirm = null }
        )
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loading...")
    }
}

@Composable
fun OnError(message: String, retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text("Retry")
        }
    }
}

@Composable
fun MhsLayout(
    mahasiswa: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit,
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mahasiswa) { mahasiswa ->
            MhsCard(
                mahasiswa = mahasiswa,
                modifier = Modifier
                    .fillMaxWidth(),
                onDeleteClick = {
                    onDeleteClick(mahasiswa)
                },
                onDetailClick = onDetailClick
            )
        }
    }


}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa,
    modifier: Modifier = Modifier,
    onDeleteClick: (String) -> Unit,
    onDetailClick: (String) -> Unit
) {
    Card(
        modifier = modifier.clickable { onDetailClick(mahasiswa.nim) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.weight(1f))
//                IconButton(onClick = { onDeleteClick(mahasiswa.nim) }) {
//                    Icon(
//                        imageVector = Icons.Default.Delete, contentDescription = null,
//                    )
//                }
                Text(
                    text = mahasiswa.nim,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = mahasiswa.alamat,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = mahasiswa.judul_skripsi,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { onDeleteCancel() },
        title = { Text("Delete Data") },
        text = { Text("Apakah anda yakin ingin menghapus data?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) { Text("Yes") }
        }
    )
}