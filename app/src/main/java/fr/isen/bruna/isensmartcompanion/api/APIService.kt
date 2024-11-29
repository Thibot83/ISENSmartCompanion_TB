package fr.isen.bruna.isensmartcompanion.api


import fr.isen.bruna.isensmartcompanion.composants.Event
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("events.json") // URL partielle, car l'URL complète sera ajoutée dans Retrofit.
    fun getEvents(): Call<List<Event>>
}