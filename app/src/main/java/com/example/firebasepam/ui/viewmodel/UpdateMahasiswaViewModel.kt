package com.example.firebasepam.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.navigasi.DestinasiUpdate
import com.example.firebasepam.repository.MahasiswaRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdateMahasiswaViewModel(
    savedStateHandle: SavedStateHandle,
    private val mahasiswaRepository: MahasiswaRepository
) : ViewModel(){
    var updateUIState by mutableStateOf(MahasiswaUIState())
        private set

    private  val nim: String = checkNotNull(savedStateHandle[DestinasiUpdate.nim])
    init {
        viewModelScope.launch {
            updateUIState = mahasiswaRepository.getMahasiswa(nim)
                .filterNotNull()
                .first()
                .toUIStateMahasiswa()
        }
    }

    fun updateState(mahasiswaEvent: MahasiswaEvent){
        updateUIState = updateUIState.copy(
            Mahasiswa = mahasiswaEvent,
        )
    }

    fun validateFields(): Boolean{
        val event = updateUIState.MahasiswaEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "Nim mahasiswa tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "Nama Mahasiswa tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "alamat mahasiswa tidak boleh kosong",
            jenisKelamin = if (event.jenisKelamin.isNotEmpty()) null else "Jenis Kelamin tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "kelas tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "Angkatan tidak boleh kosong",
            judul = if (event.judul.isNotEmpty()) null else "Judul tidak boleh kosong",
            dosen1 = if (event.dosen1.isNotEmpty()) null else "Dosen pembimbing 1 tidak boleh kosong",
            dosen2 = if (event.dosen2.isNotEmpty()) null else "Dosen pembimbing 2 tidak boleh kosong"
        )
        updateUIState = updateUIState.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun updateData(){
        val currentEvent = updateUIState.MahasiswaEvent

        if (validateFields()){
            viewModelScope.launch {
                try {
                    mahasiswaRepository.updateMahasiswa(currentEvent.toMahasiswaModel())
                    updateUIState = updateUIState.copy(
                        snackBarMesssage = "Data berhasil diupdate",
                        MahasiswaEvent = MahasiswaEvent(),
                        isEntryValid = FormErrorState()
                    )
                    println("snackBarMessage diatur: ${updateUIState.
                    snackBarMesssage}")
                } catch (e: Exception){
                    updateUIState = updateUIState.copy(
                        snackBarMesssage = "Data gagal diupdate"
                    )
                }
            }
        } else {
            updateUIState = updateUIState.copy(
                snackBarMesssage = "Data gagal diupdate"
            )
        }
    }
    fun resetSnackBarMessage(){
        updateUIState = updateUIState.copy(snackBarMesssage = null)
    }
}

fun  Mahasiswa.toUIStateMahasiswa(): InsertUiState = InsertUiState(
        MahasiswaEvent =  this.toDetailUiEvent(),
)