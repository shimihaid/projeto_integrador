package com.example.listachamada.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TurmaDao {

    @Query("SELECT * FROM turmas ORDER BY nome ASC")
    fun listarTurmas(): Flow<List<Turma>>

    @Insert
    suspend fun inserir(turma: Turma): Long

    @Update
    suspend fun atualizar(turma: Turma)

    @Delete
    suspend fun deletar(turma: Turma)

    @Query("SELECT * FROM turmas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Turma?
}
