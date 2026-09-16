package com.example.listachamada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.ChamadaRepository
import com.example.listachamada.data.Problema
import kotlinx.coroutines.launch

class AlunoDetalhesViewModel(private val repository: ChamadaRepository) : ViewModel() {
    fun inserirProblemas(alunoId: Long, problema: String) = viewModelScope.launch { repository.adicionarProblema(problema, alunoId) }
    fun listarFaltas(alunoId: Long) = repository.listarFaltas(alunoId)
    fun listarProblemas(alunoId: Long) = repository.listarProblemas(alunoId)
}
