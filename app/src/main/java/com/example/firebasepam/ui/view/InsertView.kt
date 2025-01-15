@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.firebasepam.ui.view

import android.provider.MediaStore.Audio.Radio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebasepam.ui.viewmodel.FormErrorState
import com.example.firebasepam.ui.viewmodel.FormState
import com.example.firebasepam.ui.viewmodel.HomeUiState
import com.example.firebasepam.ui.viewmodel.InsertUiState
import com.example.firebasepam.ui.viewmodel.InsertViewModel
import com.example.firebasepam.ui.viewmodel.MahasiswaEvent
import com.example.firebasepam.ui.viewmodel.PenyediaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertMahasiswaView(
    onBack: () -> Unit,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val uiState = viewModel.uiState
    val uiEvent = viewModel.uiEvent
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope ()

    LaunchedEffect(uiState) {
        when (uiState){
            is FormState.Success -> {
                println(
                    "Insert,hsView: uiStet is FormState.Success, navigate to home" + uiState.message
                )
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(uiState.message)
                }
                delay(700)
                onNavigate()
                viewModel.resetSnackBarMessage()
            }
            is FormState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(uiState.message)
                }
            }
            else -> Unit
        }
    }

    Scaffold (
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tambah Mahasiswa") },
                navigationIcon = {
                    Button(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ){ padding ->
        Column (
            modifier = modifier.fillMaxSize().padding(padding).padding(16.dp)
        ){
            InsertBodyMahasiswa(
                uiState = uiEvent,
                homeUiState = uiState,
                onValueChange = {updatedEvent ->
            viewModel.updateState(updatedEvent)},
            onClick = {
                if (viewModel.validateFields()){
                    viewModel.insertMahasiswa()
                }
            }
            )
        }
    }
}

@Composable
fun InsertBodyMahasiswa (
    modifier: Modifier = Modifier,
    onValueChange: (MahasiswaEvent) -> Unit,
    uiState: InsertUiState,
    onClick: () -> Unit,
    homeUiState: FormState
){
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ){
        FormMahasiswa(
            mahasiswaEvent = uiState.insertUiEvent,
            onValueChange = onValueChange,
            errorState = uiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = homeUiState !is FormState.Loading,
        ) {
            if (homeUiState is FormState.Loading){
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp).padding(end = 8.dp)
                )
                Text("Loading")
            }else{
                Text("Add")
            }
        }
    }
}

@Composable
fun FormMahasiswa(
    mahasiswaEvent: MahasiswaEvent = MahasiswaEvent(),
    onValueChange: (MahasiswaEvent) -> Unit,
    errorState: FormErrorState = FormErrorState(),
    modifier: Modifier = Modifier
){
    val jenisKelamin = listOf("Laki-Laki", "Perempuan")
    val kelas = listOf("A", "B", "C", "D", "E")

    Column (
        modifier = modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.nama,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(nama = it))
            },
            label = { Text("Nama") },
            isError = errorState.nama != null,
            placeholder = { Text("Masukkan nama") },
        )
        Text(
            text = errorState.nama ?: "",
            color = Color.Red
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.nim,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(nim = it))
            },
            label = { Text("Nim") },
            isError = errorState.nim != null,
            placeholder = { Text("Masukkan nim") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = errorState.nim ?: "",
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Jenis Kelamin")
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
            jenisKelamin.forEach{
                jenisKelamin ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    RadioButton(
                        selected = mahasiswaEvent.jenisKelmain == jenisKelamin,
                        onClick = {
                            onValueChange(mahasiswaEvent.copy(jenisKelmain = jenisKelamin))
                        },
                    )
                    Text(
                        text = jenisKelamin,
                    )
                }
            }
        }
        Text(
            text = errorState.jenisKelamin ?: "",
            color = Color.Red
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.alamat,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(alamat = it))
            },
            label = { Text("Alamat") },
            isError = errorState.alamat != null,
            placeholder = { Text("masukkan alamat") },
        )
        Text(text = errorState.alamat ?: "", color = Color.Red)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Kelas")
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
            kelas.forEach{
                    kelas ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    RadioButton(
                        selected = mahasiswaEvent.kelas == kelas,
                        onClick = {
                            onValueChange(mahasiswaEvent.copy(kelas = kelas))
                        },
                    )
                    Text(
                        text = kelas,
                    )
                }
            }
        }
        Text(
            text = errorState.kelas ?: "",
            color = Color.Red
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.angkatan,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(angkatan = it))
            },
            label = { Text("Angkatan") },
            isError = errorState.angkatan != null,
            placeholder = { Text("Masukkan angkatan") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = errorState.angkatan ?: "",
            color = Color.Red
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.judul,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(judul = it))
            },
            label = { Text("Judul") },
            isError = errorState.judul != null,
            placeholder = { Text("Masukkan Judul Skripsi") },
        )
        Text(
            text = errorState.judul ?: "",
            color = Color.Red
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.dosen1,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(dosen1 =  it))
            },
            label = { Text("Nama Dosen") },
            isError = errorState.dosen1 != null,
            placeholder = { Text("Masukkan Nama Dosen pembimbing1") },
        )
        Text(
            text = errorState.dosen1 ?: "",
            color = Color.Red
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = mahasiswaEvent.dosen2,
            onValueChange = {
                onValueChange(mahasiswaEvent.copy(dosen2 = it))
            },
            label = { Text("Nama dosen2") },
            isError = errorState.dosen2 != null,
            placeholder = { Text("Masukkan Nama Dosen Pembimbing 2") },
        )
        Text(
            text = errorState.dosen2 ?: "",
            color = Color.Red
        )
    }
}