package com.example.listachamada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.Aluno
import com.example.listachamada.data.AlunoResumo
import com.example.listachamada.data.ChamadaRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlunoListViewModel(private val repository: ChamadaRepository) : ViewModel() {

    // Data de hoje no formato "yyyy-MM-dd", calculada uma vez quando a
    // ViewModel é criada (ao abrir a tela da turma).
    private val hoje: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun listarAlunos(turmaId: Long) = repository.listarAlunosComResumo(turmaId, hoje)

    /** Alunos já cadastrados (de outras turmas ou sem turma) que podem ser adicionados aqui. */
    fun listarAlunosDisponiveis(turmaId: Long) = repository.listarAlunosDisponiveisParaTurma(turmaId)

    /** Cria um aluno totalmente novo e já o coloca nesta turma. */
    fun criarAluno(nome: String, numero: String?, numeroPais: String?, dataNasc: String, turmaId: Long) {
        if (nome.isBlank()) return
        viewModelScope.launch {
            repository.criarAlunoNaTurma(nome.trim(), numero ?: "", numeroPais ?: "", dataNasc, turmaId)
        }
    }

    /** Adiciona um aluno JÁ EXISTENTE (de outra turma, por exemplo) a esta turma. */
    fun matricularAlunoExistente(aluno: Aluno, turmaId: Long) {
        viewModelScope.launch {
            repository.matricularAluno(aluno.id, turmaId)
        }
    }

    /** Remove o aluno só desta turma — ele continua cadastrado e nas outras turmas. */
    fun removerDaTurma(aluno: AlunoResumo, turmaId: Long) {
        viewModelScope.launch {
            repository.desmatricularAluno(aluno.id, turmaId)
        }
    }

    /** Marca o aluno como presente ou faltoso HOJE, nesta turma. */
    fun registrarPresenca(aluno: AlunoResumo, turmaId: Long, presente: Boolean) {
        viewModelScope.launch {
            repository.registrarPresenca(aluno.id, turmaId, hoje, presente)
        }
    }
}
