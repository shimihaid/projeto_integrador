package com.example.listachamada.data

/**
 * Não é uma tabela — é o "resultado pronto" de uma consulta que já junta
 * o aluno com o total de presenças, total de faltas, e o status de hoje
 * (true = presente, false = faltou, null = ainda não teve chamada hoje).
 */
data class AlunoResumo(
    val id: Long,
    val nome: String,
    val numeroPais: String,
    val numero: String,
    val dataNascimento:String,
    val turmaId: Long,
    val presencas: Int,
    val faltas: Int,
    val statusHoje: Boolean?
)
