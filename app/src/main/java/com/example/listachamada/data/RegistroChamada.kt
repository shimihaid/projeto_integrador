package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Um registro de presença/falta de UM aluno em UM dia.
 * A combinação (alunoId, data) é única: só pode existir um registro
 * por aluno por dia (marcar de novo no mesmo dia substitui o anterior).
 */
@Entity(
    tableName = "registros_chamada",
    foreignKeys = [
        ForeignKey(
            entity = Aluno::class,
            parentColumns = ["id"],
            childColumns = ["alunoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["alunoId", "data"], unique = true)]
)
data class RegistroChamada(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alunoId: Long,
    val data: String, // formato "yyyy-MM-dd"
    val presente: Boolean
)
