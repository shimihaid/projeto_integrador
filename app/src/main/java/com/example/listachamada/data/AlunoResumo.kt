package com.example.listachamada.data

/**
 * "Resultado pronto" de uma consulta que junta o aluno com o total de
 * presenças/faltas NUMA TURMA ESPECÍFICA (a turma que está sendo exibida
 * na tela) e o status de hoje NAQUELA turma.
 * (true = presente, false = faltou, null = ainda não teve chamada hoje)
 */
data class AlunoResumo(
    val id: Long,
    val nome: String,
    val numeroPais: String,
    val numero: String,
    val dataNascimento: String,
    val presencas: Int,
    val faltas: Int,
    val statusHoje: Boolean?
)

/**
 * Usado no dashboard do aluno: uma turma em que ele está matriculado,
 * junto com as presenças/faltas dele NAQUELA turma.
 */
data class TurmaComFrequencia(
    val id: Long,
    val nome: String,
    val diaSemana: String?,
    val horaInicio: String?,
    val horaFim: String?,
    val presencas: Int,
    val faltas: Int
)
