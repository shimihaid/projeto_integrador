package com.example.listachamada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.Aluno
import com.example.listachamada.data.ChamadaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel do dashboard do aluno: dados cadastrais, turmas em que ele
 * está matriculado (com presenças/faltas em cada uma) e os "problemas"
 * registrados. As faltas detalhadas (datas) de uma turma específica são
 * buscadas à parte, quando o usuário toca numa turma (ver listarFaltas).
 */
class AlunoDetalhesViewModel(private val repository: ChamadaRepository) : ViewModel() {

    private val _aluno = MutableStateFlow<Aluno?>(null)
    val aluno: StateFlow<Aluno?> = _aluno

    fun carregarAluno(alunoId: Long) {
        viewModelScope.launch {
            _aluno.value = repository.buscarAluno(alunoId)
        }
    }

    fun listarTurmasDoAluno(alunoId: Long) = repository.listarTurmasDoAluno(alunoId)

    fun listarFaltas(alunoId: Long, turmaId: Long) = repository.listarFaltas(alunoId, turmaId)

    fun inserirProblemas(alunoId: Long, problema: String) =
        viewModelScope.launch { repository.adicionarProblema(problema, alunoId) }

    fun listarProblemas(alunoId: Long) = repository.listarProblemas(alunoId)

    fun deletarAluno(alunoId: Long, aoConcluir: () -> Unit) {
        viewModelScope.launch {
            repository.deletarAluno(alunoId)
            aoConcluir()
        }
    }
}
