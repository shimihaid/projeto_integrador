package com.example.listachamada.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listachamada.data.ChamadaRepository

class ViewModelFactory(private val application: Application,private val repository: ChamadaRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TurmaListViewModel::class.java) ->
                TurmaListViewModel(repository) as T
            modelClass.isAssignableFrom(AlunoListViewModel::class.java) ->
                AlunoListViewModel(repository) as T
            modelClass.isAssignableFrom(AlunoDetalhesViewModel::class.java) ->
                AlunoDetalhesViewModel(repository) as T
            modelClass.isAssignableFrom(ConfigViewModel::class.java) ->
                ConfigViewModel(application, repository) as T
            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
