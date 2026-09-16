package com.example.listachamada.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroChamadaDao {

    // REPLACE: como (alunoId, data) é único, marcar de novo no mesmo dia
    // substitui o registro anterior em vez de dar erro de duplicado.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrar(registro: RegistroChamada)

    @Query("SELECT * FROM registros_chamada WHERE alunoId = :alunoId AND turmaId = :turmaId AND presente = 0 ORDER BY data DESC")
    fun listarFaltas(alunoId: Long, turmaId: Long): Flow<List<RegistroChamada>>
}
