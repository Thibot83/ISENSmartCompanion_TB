package fr.isen.bruna.isensmartcompanion.composants

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.bruna.isensmartcompanion.R
import fr.isen.bruna.isensmartcompanion.database.QuestionResponse
import fr.isen.bruna.isensmartcompanion.database.QuestionResponseDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dao: QuestionResponseDao) {
    // États pour stocker les données
    var searchText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<Pair<String, String>>()) } // Liste des messages (question/réponse)
    var userInput by remember { mutableStateOf("") } // Stocke la question posée par l'utilisateur
    val questionsAndResponses = remember { mutableStateListOf<Pair<String, String>>() } // Historique
    val coroutineScope = rememberCoroutineScope() // Pour exécuter des tâches suspendues
    // Contexte pour afficher le Toast
    val context = LocalContext.current
    // Interface utilisateur
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF4B4B4B))
            ) {
                // Image en haut
                Image(
                    painter = painterResource(id = R.drawable.mon_image),
                    contentDescription = "Image en haut",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(top = 16.dp)
                )

                // Texte "Smart Companion"
                Text(
                    text = "Smart Companion",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )

                // Liste des messages (Questions / Réponses) avec un LazyColumn pour le slider
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(chatMessages) { (question, answer) ->
                        // Question de l'utilisateur avec fond personnalisé
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFA8D5BA), RoundedCornerShape(8.dp)) // Fond bleu clair pour l'utilisateur
                        ) {
                            Text(
                                text = "Utilisateur: $question",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(8.dp) // Espacement à l'intérieur de la Box
                            )
                        }

                        // Réponse de l'assistant avec fond personnalisé (plus décalé à droite)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFF87CEEB), RoundedCornerShape(8.dp)) // Fond jaune clair pour l'assistant
                        ) {
                            Text(
                                text = "Assistant: $answer",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light),
                                modifier = Modifier.padding(start = 90.dp, bottom = 8.dp) // Décalage ajusté
                            )
                        }
                    }
                }

                // Barre de recherche et bouton en bas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Barre de recherche
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Posez une question...") },
                        textStyle = TextStyle(fontSize = 14.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = Color.Black,
                            focusedIndicatorColor = Color.Green,
                            unfocusedIndicatorColor = Color.Red
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 1.dp),
                        singleLine = true
                    )

                    // Bouton
                    Button(
                        onClick = {
                            if (searchText.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        // Appeler une fonction (comme generateText) pour obtenir une réponse
                                        val geminiResponse = generateText(searchText)

                                        // Ajouter la question et la réponse dans la liste
                                        chatMessages = chatMessages + (searchText to geminiResponse)

                                        // Insérer dans la base de données
                                        dao.insert(QuestionResponse(
                                            question = searchText,
                                            response = geminiResponse
                                        ))

                                        // Réinitialiser le champ de saisie après l'envoi
                                        searchText = ""

                                        // Afficher un message de confirmation
                                        Toast.makeText(context, "Question envoyée", Toast.LENGTH_SHORT).show()


                                    } catch (e: Exception) {
                                        // Gérer les erreurs et afficher un message d'erreur
                                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                // Afficher un message si le champ est vide
                                Toast.makeText(context, "Veuillez entrer une question.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Soumettre")
                    }
                }
            }
        }
    )
}

