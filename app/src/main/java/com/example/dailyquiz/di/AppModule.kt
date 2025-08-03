package com.example.dailyquiz.di

import androidx.room.Room
import com.example.dailyquiz.data.local.AppDatabase
import com.example.dailyquiz.data.remote.OpenTdbApi
import com.example.dailyquiz.data.repository.QuizRepository
import com.example.dailyquiz.viewmodel.QuizViewModel
import com.example.dailyquiz.viewmodel.ResultsViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .client(get<OkHttpClient>())
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .build()
    }
    single {
        get<Retrofit>().create(OpenTdbApi::class.java)
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "daily_quiz_db"
        ).build()
    }
    single {
        get<AppDatabase>().quizDao()
    }

    single {
        QuizRepository(api = get(), dao = get())
    }

    viewModel { QuizViewModel(quizRepository = get()) }
    viewModel { ResultsViewModel(savedStateHandle = get()) }
}