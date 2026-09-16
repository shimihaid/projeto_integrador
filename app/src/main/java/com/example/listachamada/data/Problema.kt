package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "problemas",
    foreignKeys = [
        ForeignKey(
            entity = Aluno::class,
            parentColumns = ["id"],
            childColumns = ["alunoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("alunoId")]
)
data class Problema(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val descricao: String,

    val alunoId: Long
)