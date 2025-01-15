package com.example.firebasepam.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.repository.MahasiswaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class HomeViewModel(private val mahasiswa: MahasiswaRepository): ViewModel(){
    var mhsUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getMahasiswa()
    }

    fun getMahasiswa(){
        viewModelScope.launch {
            mahasiswa.getMahasiswa(nim = "")
                .onStart {
                    mhsUiState = HomeUiState.Loading
                }
                .catch {
                    mhsUiState = HomeUiState.Error(it)
                }
                .collect{
                    mhsUiState = if (it.isEmpty()){
                        HomeUiState.Error(Exception("Belum ada daftar mahasiswa"))
                    }else{
                        HomeUiState.Success(it)
                    }
                }
        }
    }

    fun deleteMahasiswa(mahasiswa: Mahasiswa){
        viewModelScope.launch {
            try {
                mahasiswa.deleteMahasiswa(mahasiswa.toString())
            }
            catch (e:Exception){
                mhsUiState = HomeUiState.Error(e)
            }
        }
    }

    fun insertMahasiswa(mahasiswa: Mahasiswa){

    }
    fun updateMahasiswa(nim:String, mahasiswa: Mahasiswa){

    }
    fun getMahasiswabyNIM(nim: String) {

    }
}

sealed class HomeUiState{
    data class Success(val mahasiswa: List<Mahasiswa>) : HomeUiState()
    data class Error(val message: Throwable) : HomeUiState()
    object Loading : HomeUiState()
}