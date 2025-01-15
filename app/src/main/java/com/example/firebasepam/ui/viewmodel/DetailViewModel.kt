package com.example.firebasepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.repository.MahasiswaRepository
import com.example.firebasepam.repository.NetworkMahasiswaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val mahasiswaRepository: MahasiswaRepository = NetworkMahasiswaRepository(
        FirebaseFirestore.getInstance())
) : ViewModel() {

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    fun getMahasiswaById(nim: String) {
        viewModelScope.launch {
            try {
                mahasiswaRepository.getMahasiswaById(nim)
                    .collect { mahasiswa ->
                        _detailUiState.value = DetailUiState.Success(mahasiswa)
                    }
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun deleteMhs(nim: String) {
        viewModelScope.launch {
            try {
                mahasiswaRepository.deleteMahasiswa(nim)
            } catch (e: Exception) {
            }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val mahasiswa: Mahasiswa) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
