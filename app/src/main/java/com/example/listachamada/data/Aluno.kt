package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "alunos",
    foreignKeys = [
        ForeignKey(
            entity = Turma::class,
            parentColumns = ["id"],
            childColumns = ["turmaId"],
            onDelete = ForeignKey.CASCADE // ao excluir a turma, os alunos dela são excluídos junto
        )
    ],
    indices = [Index("turmaId")]
)
data class Aluno(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val numeroPais: String,
    val numero: String,
    val dataNascimento: String,
    val turmaId: Long,


    // O contador de faltas não fica mais aqui — agora é calculado a partir
    // da tabela "registros_chamada" (ver RegistroChamada.kt e AlunoResumo.kt).
){
}
