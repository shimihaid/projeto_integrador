package com.example.listachamada.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlunoDao {

    @Insert
    suspend fun inserir(aluno: Aluno): Long

    @Query("DELETE FROM alunos WHERE id = :id")
    suspend fun deletarPorId(id: Long)

    @Query("SELECT * FROM alunos WHERE id = :id")
    suspend fun buscarPorId(id: Long): Aluno?

    @Query("SELECT * FROM alunos ORDER BY nome ASC")
    fun listarTodos(): Flow<List<Aluno>>

    /**
     * Alunos matriculados numa turma, já com presenças/faltas NAQUELA
     * turma (calculadas na hora) e o status de hoje NAQUELA turma.
     * "hoje" é passado de fora (do ViewModel) no formato "yyyy-MM-dd".
     */
    @Query(
        """
        SELECT
            a.id AS id,
            a.nome AS nome,
            a.numeroPais AS numeroPais,
            a.numero AS numero,
            a.dataNascimento AS dataNascimento,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = a.id AND r.turmaId = :turmaId AND r.presente = 1) AS presencas,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = a.id AND r.turmaId = :turmaId AND r.presente = 0) AS faltas,
            (SELECT r.presente FROM registros_chamada r WHERE r.alunoId = a.id AND r.turmaId = :turmaId AND r.data = :hoje LIMIT 1) AS statusHoje
        FROM alunos a
        INNER JOIN alunos_turmas vinculo ON vinculo.alunoId = a.id
        WHERE vinculo.turmaId = :turmaId
        ORDER BY a.nome ASC
        """
    )
    fun listarComResumo(turmaId: Long, hoje: String): Flow<List<AlunoResumo>>

    /**
     * Alunos já cadastrados (em qualquer turma, ou em nenhuma) que ainda
     * NÃO estão matriculados na turma informada — usado na tela de
     * "adicionar aluno existente a essa turma".
     */
    @Query(
        """
        SELECT * FROM alunos
        WHERE id NOT IN (SELECT alunoId FROM alunos_turmas WHERE turmaId = :turmaId)
        ORDER BY nome ASC
        """
    )
    fun listarDisponiveisParaTurma(turmaId: Long): Flow<List<Aluno>>
}
