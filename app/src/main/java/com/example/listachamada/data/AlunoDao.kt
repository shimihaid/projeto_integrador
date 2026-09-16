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

    /**
     * Lista os alunos de uma turma já com presenças/faltas totais
     * (calculadas na hora, via subconsultas) e o status de hoje.
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
            a.turmaId AS turmaId,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = a.id AND r.presente = 1) AS presencas,
            (SELECT COUNT(*) FROM registros_chamada r WHERE r.alunoId = a.id AND r.presente = 0) AS faltas,
            (SELECT r.presente FROM registros_chamada r WHERE r.alunoId = a.id AND r.data = :hoje LIMIT 1) AS statusHoje
        FROM alunos a
        WHERE a.turmaId = :turmaId
        ORDER BY a.nome ASC
        """
    )
    fun listarComResumo(turmaId: Long, hoje: String): Flow<List<AlunoResumo>>
}
