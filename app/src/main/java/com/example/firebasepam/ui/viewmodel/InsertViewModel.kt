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
) : ViewModel(){
    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set
    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set

    //memperbarui state berdasarkan input pengguna
    fun updateState(mahasiswaEvent: MahasiswaEvent){
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }

    //validasi data input pengguna
    fun validateFields(): Boolean{
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "nim tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "nama tidak boleh kosong",
            jenisKelamin = if (event.jenisKelmain.isNotEmpty()) null else "jenis kelamin tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "alamat tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "kelas tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "angkatan tidak boleh kosong",
            judul = if (event.judul.isNotEmpty()) null else "judul skripsi tidak boleh kosong",
            dosen1 = if (event.dosen1.isNotEmpty()) null else "Nama Dosen pembimbing 1 tidak boleh kosong",
            dosen2 = if (event.dosen2.isNotEmpty()) null else "Nama Dosen pembimbing 2 tidak boleh kosong",
            )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMahasiswa(){
        if (validateFields()){
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMahasiswa(uiEvent.insertUiEvent.toMahasiswaModel())
                    uiState = FormState.Success("Data berhasil disimpan")
                } catch (e:Exception){
                    uiState = FormState.Error("data gagal disimpan")
                }
            }
        } else{
            uiState = FormState.Error("data tidak valid")
        }
    }

    fun resetForm(){
        uiEvent = InsertUiState()
        uiState = FormState.Idle
    }
    fun resetSnackBarMessage(){
        uiState = FormState.Idle
    }
}

sealed class FormState{
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
    val jenisKelamin: String? = null,
    val alamat: String? = null,
    val kelas: String? = null,
    val angkatan: String? = null,
    val judul: String? = null,
    val dosen1: String? = null,
    val dosen2: String? = null,
){
    fun isValid(): Boolean{
        return nim == null && nama == null && jenisKelamin == null && alamat == null &&
                alamat == null && kelas == null && angkatan == null && judul == null && dosen1 == null && dosen2 == null
    }
}

//data class variabel yang menyimpan data input form
data class MahasiswaEvent(
    val nim: String = "",
    val nama: String = "",
    val jenisKelmain: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val angkatan: String = "",
    val judul: String = "",
    val dosen1: String = "",
    val dosen2: String = ""
)

//menyimpan input form ke dalam entity
fun MahasiswaEvent.toMahasiswaModel(): Mahasiswa = Mahasiswa(
    nim = nim,
    nama = nama,
    jenisKelamin = jenisKelmain,
    alamat = alamat,
    kelas = kelas,
    angkatan = angkatan,
    judul = judul,
    dosen1 = dosen1,
    dosen2 = dosen2
)