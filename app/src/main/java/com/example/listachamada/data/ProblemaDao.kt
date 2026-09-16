package com.example.listachamada.data

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import kotlinx.coroutines.flow.Flow

@Dao
interface ProblemaDao {
    @Insert
    suspend fun inserir(problema: Problema): Long

    @Query("DELETE FROM problemas WHERE id = :id")
    suspend fun deletarProblema(id: Long)

    @Query("SELECT * FROM problemas WHERE alunoId = :alunoId")
    fun listarProblemas(alunoId: Long): Flow<List<Problema>>

}