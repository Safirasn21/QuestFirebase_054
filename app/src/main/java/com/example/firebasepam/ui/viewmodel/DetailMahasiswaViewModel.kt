package com.example.firebasepam.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.navigasi.DestinasiDetail
import com.example.firebasepam.repository.MahasiswaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailMahasiswaViewModel (
    savedStateHandle: SavedStateHandle,
    private val mahasiswaRepository: MahasiswaRepository,
) : ViewModel(){
    private val nim: String = checkNotNull(savedStateHandle[DestinasiDetail.nim])

    val detailUIState: StateFlow<DetailUiState> = mahasiswaRepository.getMahasiswa(nim)
        .filterNotNull()
        .map {
            DetailUiState(
                detailUiEvent = it.toDetailUiEvent(),
                isLoading = false,
            )
        }
        .onStart {
            emit(DetailUiState(isLoading = true))
            delay(600)
        }
        .catch {
            emit(
                DetailUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = it.message ?: "Terjadi kesalahan",
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000),
            initialValue = DetailUiState(
                isLoading = true,
            ),
        )
    fun deleteMahasiswa(){
        detailUIState.value.detailUiEvent.toMahasiswaModel().let{
            viewModelScope.launch {
                mahasiswaRepository.deleteMahasiswa(nim)
            }
        }
    }
}


data class DetailUiState(
    val detailUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
){
    val isUiEventEmpty: Boolean
        get() = detailUiEvent == MahasiswaEvent()
    val isUiEventNotEmpty: Boolean
        get() = detailUiEvent != MahasiswaEvent()
}

fun Mahasiswa.toDetailUiEvent(): MahasiswaEvent{
    return MahasiswaEvent(
        nim = nim,
        nama = nama,
        alamat = alamat,
        jenisKelmain = jenisKelamin,
        kelas = kelas,
        angkatan = angkatan,
        judul = judul,
        dosen1 = dosen1,
        dosen2 = dosen2
    )
}