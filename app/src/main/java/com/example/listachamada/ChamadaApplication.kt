package com.example.listachamada

import android.app.Application
import com.example.listachamada.data.AppDatabase
import com.example.listachamada.data.ChamadaRepository

class ChamadaApplication : Application() {

    lateinit var repository: ChamadaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = ChamadaRepository(db)
    }
}
