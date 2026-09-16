package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Um registro de presença/falta de UM aluno em UMA turma em UM dia.
 * A combinação (alunoId, turmaId, data) é única: como um aluno pode
 * estar em várias turmas, ele pode ter uma chamada diferente em cada
 * turma no mesmo dia (ex.: presente na turma de terça, falta na de quinta).
 */
@Entity(
    tableName = "registros_chamada",
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
    indices = [Index(value = ["alunoId", "turmaId", "data"], unique = true)]
)
data class RegistroChamada(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alunoId: Long,
    val turmaId: Long,
    val data: String, // formato "yyyy-MM-dd"
    val presente: Boolean
)
