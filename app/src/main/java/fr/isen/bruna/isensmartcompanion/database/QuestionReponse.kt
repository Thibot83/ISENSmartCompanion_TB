package fr.isen.bruna.isensmartcompanion.database

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.icu.util.Calendar

@Entity(tableName = "questions_response")
data class QuestionResponse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val response: String,
    val date: String = getCurrentDate()

)
@SuppressLint("NewApi", "SimpleDateFormat")
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy") // Format souhaité
    val calendar = Calendar.getInstance()
    return sdf.format(calendar.time)
}

