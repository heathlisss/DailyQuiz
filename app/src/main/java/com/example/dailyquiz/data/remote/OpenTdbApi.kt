package com.example.dailyquiz.data.remote

import com.example.dailyquiz.data.remote.TriviaApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTdbApi {

    @GET("api.php?type=multiple")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 5,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String
    ): Response<TriviaApiResponse>
}