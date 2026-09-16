package com.example.listachamada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.ChamadaRepository
import com.example.listachamada.data.Turma
import kotlinx.coroutines.launch

class TurmaListViewModel(private val repository: ChamadaRepository) : ViewModel() {

    val turmas = repository.listarTurmas()

    fun criarTurma(nome: String, diaSemana: String?, horaInicio: String?, horaFim: String?) {
        if (nome.isBlank()) return
        viewModelScope.launch {
            repository.criarTurma(
                nome = nome.trim(),
                diaSemana = diaSemana,
                horaInicio = horaInicio?.trim()?.ifBlank { null },
                horaFim = horaFim?.trim()?.ifBlank { null }
            )
        }
    }

    fun deletarTurma(turma: Turma) {
        viewModelScope.launch {
            repository.deletarTurma(turma)
        }
    }
}
