package com.example.listachamada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun criarAluno(nome: String, numero: String?, numeroPais: String?, dataNasc: String, turmaId: Long) {
        if (nome.isBlank()) return
        viewModelScope.launch {
            repository.criarAluno(nome.trim(), numero?:"", numeroPais?:"", dataNasc, turmaId)
        }
    }

    fun deletarAluno(aluno: AlunoResumo) {
        viewModelScope.launch {
            repository.deletarAluno(aluno.id)
        }
    }

    /** Marca o aluno como presente ou faltoso HOJE. */
    fun registrarPresenca(aluno: AlunoResumo, presente: Boolean) {
        viewModelScope.launch {
            repository.registrarPresenca(aluno.id, hoje, presente)
        }
    }
}
