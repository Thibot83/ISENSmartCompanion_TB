package fr.isen.bruna.isensmartcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestionResponseDao {

    // Insérer une question-réponse dans la base de données
    @Insert
    suspend fun insert(questionResponse: QuestionResponse)

    // Récupérer toutes les questions et réponses
    @Query("SELECT * FROM questions_response")
    suspend fun getAll(): List<QuestionResponse>

    // Supprimer toutes les données
    @Query("DELETE FROM questions_response")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(questionResponse: QuestionResponse)

    @Query("DELETE FROM questions_response")
    suspend fun clearHistory()

    @Query("DELETE FROM questions_response WHERE id = :itemId")
    suspend fun deleteById(itemId: Int)
}
