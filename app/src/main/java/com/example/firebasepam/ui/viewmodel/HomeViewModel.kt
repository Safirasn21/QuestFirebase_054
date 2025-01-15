package com.example.firebasepam.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam.model.Mahasiswa
import com.example.firebasepam.repository.MahasiswaRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class HomeViewModel(private val mhs: MahasiswaRepository): ViewModel(){
    var mhsUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getMhs()
    }

    fun getMhs(){
        viewModelScope.launch {
            mhs.getMahasiswa(nim = "")
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
                mhs.deleteMahasiswa(mahasiswa.toString())
            }
            catch (e:Exception){
                mhsUiState = HomeUiState.Error(e)
            }
        }
    }


}

sealed class HomeUiState{
    data class Success(val mahasiswa: List<Mahasiswa>) : HomeUiState()
    data class Error(val message: Throwable) : HomeUiState()
    object Loading : HomeUiState()
}