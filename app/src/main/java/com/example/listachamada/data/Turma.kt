package com.example.listachamada.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turmas")
data class Turma(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    // Todos os três abaixo são opcionais (podem ficar null).
    val diaSemana: String? = null,   // ex: "Segunda-feira"
    val horaInicio: String? = null,  // ex: "19:00"
    val horaFim: String? = null      // ex: "21:00"
)
