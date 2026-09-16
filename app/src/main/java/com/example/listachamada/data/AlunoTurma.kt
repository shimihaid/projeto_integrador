package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabela de junção (N:N): representa a matrícula de um aluno numa turma.
 * Um aluno pode ter várias linhas aqui (uma por turma em que participa);
 * uma turma pode ter várias linhas (uma por aluno matriculado nela).
 */
@Entity(
    tableName = "alunos_turmas",
    primaryKeys = ["alunoId", "turmaId"],
    foreignKeys = [
        ForeignKey(
            entity = Aluno::class,
            parentColumns = ["id"],
            childColumns = ["alunoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Turma::class,
            parentColumns = ["id"],
            childColumns = ["turmaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("alunoId"), Index("turmaId")]
)
data class AlunoTurma(
    val alunoId: Long,
    val turmaId: Long
)
