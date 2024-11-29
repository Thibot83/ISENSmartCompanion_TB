package fr.isen.bruna.isensmartcompanion.composants

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.bruna.isensmartcompanion.EventDetailActivity
import fr.isen.bruna.isensmartcompanion.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


private const val PREFS_NAME = "user_preferences"
private const val NOTIFICATION_KEY = "notification_state"

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val category: String,
    val description: String,
    val location: String
)

@SuppressLint("NewApi")
@Composable
fun EventScreen() {
    val context = LocalContext.current
    val events = remember { mutableStateOf<List<Event>>(listOf()) }

    // Charger les événements lors du démarrage
    LaunchedEffect(Unit) {
        fetchEvents(events, context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {
        items(events.value) { event ->
            EventCard(event = event, onClick = {
                // Naviguer vers la page de détails
                val intent = Intent(context, EventDetailActivity::class.java).apply {
                    putExtra("eventId", event.id)
                    putExtra("eventTitle", event.title)
                    putExtra("eventCategory", event.category)
                    putExtra("eventDescription", event.description)
                    putExtra("eventLocation", event.location)
                    putExtra("eventDate", event.date)
                }
                context.startActivity(intent)
            })
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    var isNotified by remember { mutableStateOf(false) } // Suivi de l'état de la notification

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable(onClick = onClick) // Rendre le rectangle cliquable
            .background(
                color = Color(0xFFBDBDBD), // Gris clair
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f) // Faire en sorte que le texte prenne toute la place
                )

                // Ajouter l'icône de notification
                IconButton(onClick = {
                    isNotified = !isNotified // Alterner l'état de la notification
                    // Vous pouvez également stocker cet état dans SharedPreferences ici si nécessaire
                }) {
                    Icon(
                        imageVector = if (isNotified) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                        contentDescription = if (isNotified) "Notification activée" else "Notification désactivée",
                        tint = if (isNotified) Color.Green else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Catégorie : ${event.category}",
                style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Date : ${event.date}",
                style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lieu : ${event.location}",
                style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
            )
        }
    }
}


private fun fetchEvents(events: MutableState<List<Event>>, context: Context) {
    val api = RetrofitInstance.api

    api.getEvents().enqueue(object : Callback<List<Event>> {
        override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
            if (response.isSuccessful) {
                events.value = response.body()!!
            } else {
                Toast.makeText(context, "Erreur : ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Event>>, t: Throwable) {
            Toast.makeText(context, "Échec : ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

// Function to format the date in French
@SuppressLint("NewApi")
fun formatDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("fr"))
    val month = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("fr"))
    return "$dayOfWeek ${date.dayOfMonth} $month"
}
