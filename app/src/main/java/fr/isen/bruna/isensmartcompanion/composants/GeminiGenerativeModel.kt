package fr.isen.bruna.isensmartcompanion.composants

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig


val model = GenerativeModel(
    modelName = "gemini-1.5-flash-001",
    apiKey = "AIzaSyCnwvg5dvxnIKEF6HLRlZmTcyAkl5-WqsA",
    generationConfig = generationConfig {
        temperature = 0.15f
        topK = 32
        topP = 1f
        maxOutputTokens = 4096
    },
    safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
    )
)
// Fonction pour générer une réponse à partir d'un prompt
suspend fun generateText(prompt: String): String {
    return try {
        // Appel à une méthode de génération sur l'objet 'model'
        val result = model.generateContent(prompt)  // Assurez-vous d'avoir une méthode valide pour la génération de texte
        result.text ?: "Aucune réponse générée"
    } catch (e: Exception) {
        "Erreur : ${e.message}"
    }
}
