package com.example.listachamada.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.listachamada.data.AppDatabase
import com.example.listachamada.data.ChamadaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ConfigViewModel(
    application: Application,
    private val repository: ChamadaRepository
) : AndroidViewModel(application) {

    /**
     * Exporta o banco de dados para o Uri escolhido pelo usuário.
     *
     * Correção: o Room usa o modo WAL (write-ahead log) por padrão — parte
     * das alterações mais recentes pode estar só no arquivo "-wal" e ainda
     * não ter sido escrita no arquivo .db principal. Copiar só o .db sem
     * fazer isso antes gerava backups "atrasados", faltando os últimos
     * registros. Por isso fazemos um checkpoint FULL antes de copiar.
     */
    fun exportarDatabase(uri: Uri, onResultado: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val db = AppDatabase.getInstance(context)

                // Força tudo que está no WAL a ser gravado no arquivo .db principal.
                // OBS: "PRAGMA wal_checkpoint" devolve uma linha de resultado
                // (busy, log, checkpointed), então tem que ser executado com
                // query()/cursor — execSQL() só aceita comandos sem retorno,
                // e lançava SQLiteException aqui, interrompendo o export antes
                // de copiar o arquivo (por isso o backup saía com 0 bytes).
                db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(TRUNCATE)").use { cursor ->
                    cursor.moveToFirst()
                }

                val databaseFile = context.getDatabasePath("lista_chamada.db")

                val saida = context.contentResolver.openOutputStream(uri)
                    ?: throw IllegalStateException("Não foi possível abrir o destino selecionado")

                saida.use { output ->
                    databaseFile.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) { onResultado(true, null) }
            } catch (e: Exception) {
                e.printStackTrace()
                val mensagem = "${e::class.simpleName}: ${e.message}"
                withContext(Dispatchers.Main) { onResultado(false, mensagem) }
            }
        }
    }

    /**
     * Importa um backup, substituindo o banco de dados atual.
     *
     * Correções:
     * 1) Fecha a conexão do Room (db.close()) e descarta a instância em
     *    memória ANTES de sobrescrever o arquivo .db. Sem isso, o SQLite
     *    ainda tinha o arquivo antigo aberto e o import corrompia o banco
     *    ou continuava mostrando os dados de antes até reabrir o app.
     * 2) Apaga os arquivos auxiliares "-wal" e "-shm" do banco ANTERIOR:
     *    se sobrarem, o SQLite pode tentar reaplicar um WAL que não
     *    corresponde mais ao .db recém-importado, corrompendo os dados.
     */
    fun importarDatabase(uri: Uri, onResultado: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()

                AppDatabase.getInstance(context).close()
                AppDatabase.limparInstancia()

                val databaseFile = context.getDatabasePath("lista_chamada.db")

                val entrada = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalStateException("Não foi possível abrir o arquivo selecionado")

                entrada.use { input ->
                    databaseFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                File(databaseFile.path + "-wal").delete()
                File(databaseFile.path + "-shm").delete()

                withContext(Dispatchers.Main) { onResultado(true, null) }
            } catch (e: Exception) {
                e.printStackTrace()
                val mensagem = "${e::class.simpleName}: ${e.message}"
                withContext(Dispatchers.Main) { onResultado(false, mensagem) }
            }
        }
    }
}
