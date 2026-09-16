package com.example.listachamada

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.listachamada.data.AlunoResumo
import com.example.listachamada.data.Turma
import com.example.listachamada.ui.AlunoListViewModel
import com.example.listachamada.ui.AlunoDetalhesViewModel
import com.example.listachamada.ui.ConfigViewModel
import com.example.listachamada.ui.TurmaListViewModel
import com.example.listachamada.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as ChamadaApplication
        val factory = ViewModelFactory(
                application = app,
        repository = app.repository
        )

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                AppNavHost(navController, factory)
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, factory: ViewModelFactory) {
    NavHost(navController = navController, startDestination = "turmas") {
        composable("config") {

            val viewModel: ConfigViewModel = viewModel(
                factory = factory
            )

            val exportLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/octet-stream")
            ) { uri ->
                if (uri != null) {
                    viewModel.exportarDatabase(uri)
                }
            }
            val context = LocalContext.current
            val importLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    viewModel.importarDatabase(uri) { sucesso ->

                        if (sucesso) {
                            val intent = context.packageManager
                                .getLaunchIntentForPackage(context.packageName)

                            intent?.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )

                            context.startActivity(intent)
                        }
                    }
                }
            }

            TelaConfiguracoes(
                aoVoltar = {
                    navController.popBackStack()
                },
                aoExportar = {
                    exportLauncher.launch("lista_chamada_backup.db")
                },
                aoImportar = {
                    importLauncher.launch(arrayOf(
                        "application/octet-stream",
                        "application/x-sqlite3"
                    ))
                }
            )
        }

        composable("turmas") {
            val viewModel: TurmaListViewModel = viewModel(factory = factory)
            TelaTurmas(viewModel,
                aoAbrirConfig = {
                navController.navigate("config")
            },
                aoAbrirTurma = { turma ->
                navController.navigate("alunos/${turma.id}/${turma.nome}")

            }
            )
        }
        composable("alunos/{turmaId}/{turmaNome}") { backStackEntry ->
            val turmaId = backStackEntry.arguments?.getString("turmaId")?.toLongOrNull() ?: 0L
            val turmaNome = backStackEntry.arguments?.getString("turmaNome") ?: ""
            val viewModel: AlunoListViewModel = viewModel(factory = factory)
            TelaAlunos(
                viewModel = viewModel,
                turmaId = turmaId,
                turmaNome = turmaNome,
                aoVoltar = { navController.popBackStack() },
                aoAbrirFaltas = { aluno ->
                    navController.navigate("faltas/${aluno.id}/${aluno.nome}")
                },

            )
        }
        composable("faltas/{alunoId}/{alunoNome}") { backStackEntry ->
            val alunoId = backStackEntry.arguments?.getString("alunoId")?.toLongOrNull() ?: 0L
            val alunoNome = backStackEntry.arguments?.getString("alunoNome") ?: ""
            val viewModel: AlunoDetalhesViewModel = viewModel(factory = factory)
            TelaDetalhes(viewModel, alunoId, alunoNome ) {
                navController.popBackStack()
            }
        }
    }
}

private val DIAS_SEMANA = listOf(
    "Sem dia definido", "Segunda-feira", "Terça-feira", "Quarta-feira",
    "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConfiguracoes(
    aoVoltar: () -> Unit,
    aoExportar: () -> Unit,
    aoImportar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Configurações")
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Banco de dados",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    aoExportar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exportar banco de dados")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    aoImportar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Importar banco de dados")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTurmas(viewModel: TurmaListViewModel, aoAbrirTurma: (Turma) -> Unit, aoAbrirConfig:() -> Unit) {
    val turmas by viewModel.turmas.collectAsState(initial = emptyList())

    var nomeNovaTurma by remember { mutableStateOf("") }
    var diaSelecionado by remember { mutableStateOf(DIAS_SEMANA[0]) }
    var dropdownAberto by remember { mutableStateOf(false) }
    var horaInicio by remember { mutableStateOf("") }
    var horaFim by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Turmas") }, actions = {
            IconButton(onClick = aoAbrirConfig) { Icon(imageVector = Icons.Default.Settings, contentDescription = "Configurações") }
        }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = nomeNovaTurma,
                onValueChange = { nomeNovaTurma = it },
                label = { Text("Nova turma") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown de dia da semana — sempre opcional, "Sem dia definido" vira null.
            ExposedDropdownMenuBox(
                expanded = dropdownAberto,
                onExpandedChange = { dropdownAberto = it }
            ) {
                OutlinedTextField(
                    value = diaSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dia da semana (opcional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAberto) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownAberto,
                    onDismissRequest = { dropdownAberto = false }
                ) {
                    DIAS_SEMANA.forEach { dia ->
                        DropdownMenuItem(
                            text = { Text(dia) },
                            onClick = {
                                diaSelecionado = dia
                                dropdownAberto = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    label = { Text("Início (opcional)") },
                    placeholder = { Text("19:00") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaFim,
                    onValueChange = { horaFim = it },
                    label = { Text("Fim (opcional)") },
                    placeholder = { Text("21:00") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.criarTurma(
                        nome = nomeNovaTurma,
                        diaSemana = diaSelecionado.takeIf { it != DIAS_SEMANA[0] },
                        horaInicio = horaInicio,
                        horaFim = horaFim
                    )
                    nomeNovaTurma = ""
                    diaSelecionado = DIAS_SEMANA[0]
                    horaInicio = ""
                    horaFim = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar turma")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(turmas) { turma ->
                    ListItem(
                        headlineContent = { Text(turma.nome) },
                        supportingContent = descricaoHorario(turma)?.let { texto ->
                            { Text(texto) }
                        },
                        modifier = Modifier.clickable { aoAbrirTurma(turma) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deletarTurma(turma) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir turma")
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAlunos(
    viewModel: AlunoListViewModel,
    turmaId: Long,
    turmaNome: String,
    aoVoltar: () -> Unit,
    aoAbrirFaltas: (AlunoResumo) -> Unit
) {
    val alunos by viewModel.listarAlunos(turmaId).collectAsState(initial = emptyList())
    var nomeNovoAluno by remember { mutableStateOf("") }
    var novaDataNasc by remember { mutableStateOf("") }
    var novoNumero by remember { mutableStateOf("") }
    var novoNumeroPais by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(turmaNome) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
               ) {
                OutlinedTextField(
                    value = nomeNovoAluno,
                    onValueChange = { nomeNovoAluno = it },
                    label = { Text("Novo aluno") },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = novaDataNasc,
                    onValueChange = {novaDataNasc = it},
                    label = {Text("Data Nasc")},
                    placeholder = {Text("16/07/2026")},
                    modifier = Modifier.weight(1f)
                )
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = novoNumero,
                        onValueChange = { novoNumero = it },
                        label = { Text("Numero") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = novoNumeroPais,
                        onValueChange = {novoNumeroPais = it},
                        label = {Text("Numero Pais")},
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        viewModel.criarAluno(nome = nomeNovoAluno, numero = novoNumero, numeroPais = novoNumeroPais, dataNasc = novaDataNasc, turmaId = turmaId )
                        nomeNovoAluno = ""
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar aluno")
                    }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(alunos) { aluno ->
                    LinhaAluno(
                        aluno = aluno,
                        onRegistrar = { presente -> viewModel.registrarPresenca(aluno, presente) },
                        onExcluir = { viewModel.deletarAluno(aluno) },
                        onAbrirFaltas = { aoAbrirFaltas(aluno) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun LinhaAluno(
    aluno: AlunoResumo,
    onRegistrar: (presente: Boolean) -> Unit,
    onExcluir: () -> Unit,
    onAbrirFaltas: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onAbrirFaltas() }
        ) {
            Text(aluno.nome, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Presenças: ${aluno.presencas}  •  Faltas: ${aluno.faltas}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Cor da bolinha = status de HOJE, vindo direto do banco:
        // verde = presente, vermelho = faltou, cinza = ainda não teve chamada hoje.
        val corBolinha = when (aluno.statusHoje) {
            true -> Color(0xFF2E7D32)
            false -> Color.Red
            null -> Color.LightGray
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(corBolinha)
                .clickable {
                    // Clicou: se hoje já estava marcado como presente, vira falta.
                    // Se estava faltou (ou ainda não tinha chamada), vira presente.
                    val novoStatus = aluno.statusHoje != true
                    onRegistrar(novoStatus)
                }
        )
        Spacer(modifier = Modifier.width(12.dp))

        IconButton(onClick = onExcluir) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir aluno")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhes(
    viewModel: AlunoDetalhesViewModel,
    alunoId: Long,
    alunoNome: String,
    aoVoltar: () -> Unit
) {
    val faltas by viewModel.listarFaltas(alunoId).collectAsState(initial = emptyList())
    val problemas by viewModel.listarProblemas(alunoId).collectAsState(initial = emptyList())
    var novoProblema by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${alunoNome}") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =========================
            // PROBLEMAS
            // =========================
            item {
                OutlinedTextField(
                    value = novoProblema,
                    onValueChange = { novoProblema = it },
                    label = { Text("Problema") },
                    modifier = Modifier.fillMaxWidth(),

                )

                Button(
                    onClick = {
                        if (novoProblema.isNotBlank()) {
                            viewModel.inserirProblemas(
                                alunoId,
                                novoProblema
                            )

                            novoProblema = ""
                        }
                    }
                ) {
                    Text("Adicionar problema")
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Problemas",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (problemas.isEmpty()) {

                item {
                    Text(
                        text = "Nenhum problema cadastrado.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                items(problemas) { problema ->

                    ListItem(
                        headlineContent = {
                            Text(problema.descricao)
                        }
                    )

                    Divider()
                }
            }

            // =========================
            // FALTAS
            // =========================

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Faltas",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (faltas.isEmpty()) {

                item {
                    Text(
                        text = "Nenhuma falta registrada.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                items(faltas) { registro ->

                    ListItem(
                        headlineContent = {
                            Text(formatarData(registro.data))
                        }
                    )

                    Divider()
                }
            }
        }
    }
}

/**
 * Monta o texto de apoio de uma turma a partir dos campos opcionais que
 * ela tiver preenchido. Retorna null se não tiver NADA preenchido (nesse
 * caso a linha da lista não mostra subtítulo nenhum).
 */
private fun descricaoHorario(turma: Turma): String? {
    val partes = mutableListOf<String>()
    turma.diaSemana?.let { partes.add(it) }

    val inicio = turma.horaInicio
    val fim = turma.horaFim
    when {
        inicio != null && fim != null -> partes.add("$inicio às $fim")
        inicio != null -> partes.add("a partir das $inicio")
        fim != null -> partes.add("até as $fim")
    }

    return if (partes.isEmpty()) null else partes.joinToString(" • ")
}

/** Converte "yyyy-MM-dd" (formato salvo no banco) para "dd/MM/yyyy" (formato de leitura). */
private fun formatarData(dataIso: String): String {
    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val saida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        saida.format(entrada.parse(dataIso)!!)
    } catch (e: Exception) {
        dataIso
    }
}
