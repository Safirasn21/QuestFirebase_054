package com.example.firebasepam.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.MahasiswaApplication
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

    //memperbarui state berdasarkan input pengguna
    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }

    //validasi data input pengguna
    fun validateFields(): Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "nim tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "nama tidak boleh kosong",
            jenisKelamin = if (event.jenisKelmain.isNotEmpty()) null else "jenis kelamin tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "alamat tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "kelas tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "angkatan tidak boleh kosong",
        )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMahasiswa() {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMahasiswa(uiEvent.insertUiEvent.toMahasiswaModel())
                    uiState = FormState.Success("Data berhasil disimpan")
                } catch (e: Exception) {
                    uiState = FormState.Error("data gagal disimpan")
                }
            }
        } else {
            uiState = FormState.Error("data tidak valid")
        }
    }

}