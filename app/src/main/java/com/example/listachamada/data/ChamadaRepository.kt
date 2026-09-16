package com.example.listachamada.data

class ChamadaRepository(private val db: AppDatabase) {

    // Turmas
    fun listarTurmas() = db.turmaDao().listarTurmas()

    suspend fun criarTurma(nome: String, diaSemana: String?, horaInicio: String?, horaFim: String?) =
        db.turmaDao().inserir(
            Turma(nome = nome, diaSemana = diaSemana, horaInicio = horaInicio, horaFim = horaFim)
        )

    suspend fun deletarTurma(turma: Turma) = db.turmaDao().deletar(turma)

    // Alunos de uma turma (já com presenças/faltas/status de hoje NAQUELA turma)
    fun listarAlunosComResumo(turmaId: Long, hoje: String) =
        db.alunoDao().listarComResumo(turmaId, hoje)

    /** Cria um aluno novo do zero e já o matricula na turma informada. */
    suspend fun criarAlunoNaTurma(
        nome: String, numero: String?, numeroPais: String?, dataNascimento: String, turmaId: Long
    ): Long {
        val alunoId = db.alunoDao().inserir(
            Aluno(nome = nome, dataNascimento = dataNascimento, numero = numero ?: "", numeroPais = numeroPais ?: "")
        )
        db.alunoTurmaDao().matricular(AlunoTurma(alunoId = alunoId, turmaId = turmaId))
        return alunoId
    }

    /** Alunos já cadastrados que ainda não estão nessa turma. */
    fun listarAlunosDisponiveisParaTurma(turmaId: Long) =
        db.alunoDao().listarDisponiveisParaTurma(turmaId)

    /** Matricula um aluno JÁ EXISTENTE numa turma (aluno pode estar em várias). */
    suspend fun matricularAluno(alunoId: Long, turmaId: Long) =
        db.alunoTurmaDao().matricular(AlunoTurma(alunoId = alunoId, turmaId = turmaId))

    /** Tira o aluno só DESSA turma — o cadastro dele e as outras turmas continuam. */
    suspend fun desmatricularAluno(alunoId: Long, turmaId: Long) =
        db.alunoTurmaDao().desmatricular(alunoId, turmaId)

    /** Apaga o cadastro do aluno por completo (todas as turmas e todo o histórico). */
    suspend fun deletarAluno(alunoId: Long) = db.alunoDao().deletarPorId(alunoId)

    suspend fun buscarAluno(alunoId: Long) = db.alunoDao().buscarPorId(alunoId)

    /** Dashboard: turmas do aluno + presenças/faltas dele em cada uma. */
    fun listarTurmasDoAluno(alunoId: Long) = db.alunoTurmaDao().listarTurmasDoAluno(alunoId)

    // Chamada do dia (agora também por turma, já que o aluno pode estar em várias)
    suspend fun registrarPresenca(alunoId: Long, turmaId: Long, data: String, presente: Boolean) =
        db.registroChamadaDao().registrar(
            RegistroChamada(alunoId = alunoId, turmaId = turmaId, data = data, presente = presente)
        )

    /** Histórico de faltas de um aluno numa turma específica. */
    fun listarFaltas(alunoId: Long, turmaId: Long) = db.registroChamadaDao().listarFaltas(alunoId, turmaId)

    suspend fun adicionarProblema(problema: String, alunoId: Long) =
        db.problemaDao().inserir(Problema(descricao = problema, alunoId = alunoId))

    suspend fun deletarProblema(id: Long) = db.problemaDao().deletarProblema(id)

    fun listarProblemas(alunoId: Long) = db.problemaDao().listarProblemas(alunoId)
}
