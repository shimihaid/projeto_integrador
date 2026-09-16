package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Um aluno é cadastrado UMA vez só. Ele pode estar matriculado em
 * várias turmas ao mesmo tempo — esse vínculo fica na tabela
 * "alunos_turmas" (ver AlunoTurma.kt), não mais aqui.
 */
@Entity(tableName = "alunos")
data class Aluno(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val numeroPais: String,
    val numero: String,
    val dataNascimento: String
)
