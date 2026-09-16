package com.example.listachamada.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlunoTurmaDao {

    // IGNORE: se o aluno já estiver matriculado nessa turma, não faz nada
    // (evita erro de chave primária duplicada ao tentar adicionar de novo).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun matricular(vinculo: AlunoTurma)

    @Query("DELETE FROM alunos_turmas WHERE alunoId = :alunoId AND turmaId = :turmaId")
    suspend fun desmatricular(alunoId: Long, turmaId: Long)

    /**
     * Dashboard do aluno: todas as turmas em que ele está matriculado,
     * cada uma já com as presenças/faltas dele NAQUELA turma.
     */
    @Query(
        """
        SELECT
            t.id AS id,
            t.nome AS nome,
            t.diaSemana AS diaSemana,
            t.horaInicio AS horaInicio,
            t.horaFim AS horaFim,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = :alunoId AND r.turmaId = t.id AND r.presente = 1) AS presencas,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = :alunoId AND r.turmaId = t.id AND r.presente = 0) AS faltas
        FROM turmas t
        INNER JOIN alunos_turmas vinculo ON vinculo.turmaId = t.id
        WHERE vinculo.alunoId = :alunoId
        ORDER BY t.nome ASC
        """
    )
    fun listarTurmasDoAluno(alunoId: Long): Flow<List<TurmaComFrequencia>>
}
