package fr.isen.bruna.isensmartcompanion.composants

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.bruna.isensmartcompanion.database.QuestionResponse
import fr.isen.bruna.isensmartcompanion.database.QuestionResponseDao
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(dao: QuestionResponseDao) {
    val context = LocalContext.current
    var historyItems by remember { mutableStateOf(listOf<QuestionResponse>()) }

    // Utilisation de coroutineScope pour charger les données depuis la base de données
    val coroutineScope = rememberCoroutineScope()

    // Charger les données au démarrage
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            historyItems = dao.getAll() // Récupérer toutes les entrées
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // Bouton pour supprimer tout l'historique
        Button(
            onClick = {
                coroutineScope.launch {
                    dao.deleteAll()
                    historyItems = emptyList() // Effacer aussi localement
                    Toast.makeText(context, "Historique supprimé", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Supprimer tout l'historique")
        }

        // Liste des couples question/réponse
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(historyItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Question: ${item.question}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Réponse: ${item.response}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Date: ${item.date}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Icône de poubelle
                    IconButton(onClick = {
                        coroutineScope.launch {
                            dao.deleteById(item.id) // Supprimer dans la base
                            historyItems = dao.getAll()
                            Toast.makeText(context, "Message supprimé", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete, // Icône par défaut
                            contentDescription = "Supprimer",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}


