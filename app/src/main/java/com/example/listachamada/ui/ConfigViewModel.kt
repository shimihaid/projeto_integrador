package com.example.listachamada.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.AppDatabase
import com.example.listachamada.data.ChamadaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfigViewModel(
    application: Application,
    private val repository: ChamadaRepository
) : AndroidViewModel(application) {

    fun exportarDatabase(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {

            val context = getApplication<Application>()

            val databaseFile =
                context.getDatabasePath("lista_chamada.db")

            databaseFile.inputStream().use { input ->
                context.contentResolver
                    .openOutputStream(uri)
                    .use { output ->
                        requireNotNull(output)
                        input.copyTo(output)
                    }
            }
        }
    }
    fun importarDatabase(uri: Uri, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()


                val databaseFile =
                    context.getDatabasePath("lista_chamada.db")

                context.contentResolver
                    .openInputStream(uri)
                    .use { input ->

                        requireNotNull(input)

                        databaseFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                withContext(Dispatchers.Main) {
                    onResultado(true)
                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    onResultado(false)
                }
            }
        }
    }
}