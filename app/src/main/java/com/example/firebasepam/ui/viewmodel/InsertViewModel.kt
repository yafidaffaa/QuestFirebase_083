package com.example.firebasepam.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.repository.MahasiswaRepository
import kotlinx.coroutines.launch

class InsertViewModel(
    private val mhs: MahasiswaRepository
) : ViewModel() {
    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set
    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set

    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }

    fun validateFields(): Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "NIM tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "Nama tidak boleh kosong",
            jenisKelamin = if (event.jenisKelamin.isNotEmpty()) null else "Jenis Kelamin tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "Alamat tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "Kelas tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "Angkatan tidak boleh kosong",
            judul_skripsi = if (event.angkatan.isNotEmpty()) null else "Judul skripsi tidak boleh kosong",
            dosen_pembimbing1 = if (event.angkatan.isNotEmpty()) null else "DosPem 1 tidak boleh kosong",
            dosen_pembimbing2 = if (event.angkatan.isNotEmpty()) null else "DosPem 2 tidak boleh kosong"

        )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMhs() {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMahasiswa(uiEvent.insertUiEvent.toMhsModel())
                    uiState = FormState.Success("Data berhasil disimpan")
                } catch (e: Exception) {
                    uiState = FormState.Error("Data gagal disimpan")
                }
            }
        } else {
            uiState = FormState.Error("Data tidak valid")
        }
    }

    fun resetForm() {
        uiEvent = InsertUiState()
        uiState = FormState.Idle
    }

    fun resetSnackBarMessage() {
        uiState = FormState.Idle
    }
}


sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    data class Success(val message: String) : FormState()
    data class Error(val message: String) : FormState()
}

data class InsertUiState(
    val insertUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isEntryValid: FormErrorState = FormErrorState(),
)

data class FormErrorState(
    val nim: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val jenisKelamin: String? = null,
    val kelas: String? = null,
    val angkatan: String? = null,
    val judul_skripsi: String? = null,
    val dosen_pembimbing1: String? = null,
    val dosen_pembimbing2: String? = null
) {
    fun isValid(): Boolean {
        return nim == null && nama == null && jenisKelamin == null
                && alamat == null && kelas == null && angkatan == null && judul_skripsi == null && dosen_pembimbing1 == null && dosen_pembimbing2 == null
    }
}

data class MahasiswaEvent(
    val nim: String = "",
    val nama: String = "",
    val alamat: String = "",
    val jenisKelamin: String = "",
    val kelas: String = "",
    val angkatan: String = "",
    val judul_skripsi: String = "",
    val dosen_pembimbing1: String = "",
    val dosen_pembimbing2: String = ""
)

fun MahasiswaEvent.toMhsModel(): Mahasiswa = Mahasiswa(
    nim = nim,
    nama = nama,
    alamat = alamat,
    jenis_kelamin = jenisKelamin,
    kelas = kelas,
    angkatan = angkatan,
    judul_skripsi = judul_skripsi,
    dosen_pembimbing1 = dosen_pembimbing1,
    dosen_pembimbing2 = dosen_pembimbing2

)