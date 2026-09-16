package com.example.listachamada.data

import java.util.Date

class ChamadaRepository(private val db: AppDatabase) {

    // Turmas
    fun listarTurmas() = db.turmaDao().listarTurmas()
    suspend fun criarTurma(nome: String, diaSemana: String?, horaInicio: String?, horaFim: String?) =
        db.turmaDao().inserir(
            Turma(nome = nome, diaSemana = diaSemana, horaInicio = horaInicio, horaFim = horaFim)
        )
    suspend fun deletarTurma(turma: Turma) = db.turmaDao().deletar(turma)

    // Alunos (já com presenças/faltas/status de hoje calculados)
    fun listarAlunosComResumo(turmaId: Long, hoje: String) =
        db.alunoDao().listarComResumo(turmaId, hoje)

    suspend fun criarAluno(nome: String, numero: String?, numeroPais: String?, dataNascimento: String,  turmaId: Long) =
        db.alunoDao().inserir(Aluno(nome = nome, dataNascimento = dataNascimento, numero = numero?:"", numeroPais = numeroPais?:"", turmaId = turmaId))


    suspend fun deletarAluno(alunoId: Long) = db.alunoDao().deletarPorId(alunoId)

    // Chamada do dia
    suspend fun registrarPresenca(alunoId: Long, data: String, presente: Boolean) =
        db.registroChamadaDao().registrar(
            RegistroChamada(alunoId = alunoId, data = data, presente = presente)
        )

    // Histórico de faltas de um aluno específico
    fun listarFaltas(alunoId: Long) = db.registroChamadaDao().listarFaltas(alunoId)


    suspend fun adicionarProblema(problema: String, alunoId: Long) =
        db.problemaDao().inserir(Problema(descricao = problema, alunoId =  alunoId))

    suspend fun deletarProblema(id: Long) =
        db.problemaDao().deletarProblema(id);
    fun listarProblemas(alunoId: Long) = db.problemaDao().listarProblemas(alunoId)
}
