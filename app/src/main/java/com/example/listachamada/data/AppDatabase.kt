package com.example.listachamada.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Turma::class, Aluno::class, AlunoTurma::class, Problema::class, RegistroChamada::class],
    version = 6, // subiu por causa da tabela alunos_turmas e do turmaId em registros_chamada
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun turmaDao(): TurmaDao
    abstract fun alunoDao(): AlunoDao
    abstract fun alunoTurmaDao(): AlunoTurmaDao
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

        /**
         * Descarta a instância atual do Room. Precisa ser chamado depois de
         * fechar a conexão (db.close()) e ANTES de sobrescrever o arquivo
         * .db na importação de backup — senão o próximo getInstance()
         * devolveria a conexão antiga, presa no arquivo (e nos dados) de antes.
         */
        fun limparInstancia() {
            synchronized(this) {
                INSTANCE = null
            }
        }
    }
}
