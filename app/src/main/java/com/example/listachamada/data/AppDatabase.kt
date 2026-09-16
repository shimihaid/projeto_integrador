package com.example.listachamada.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Turma::class, Aluno::class, Problema::class, RegistroChamada::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun turmaDao(): TurmaDao
    abstract fun alunoDao(): AlunoDao
    abstract fun problemaDao(): ProblemaDao
    abstract fun registroChamadaDao(): RegistroChamadaDao
        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getInstance(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instancia = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "lista_chamada.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instancia
                    instancia
                }
            }

        }

}
