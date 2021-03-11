package com.example.gistlist.di

import android.app.Application
import androidx.room.Room
import com.example.gistlist.data.api.RemoteGithubApi
import com.example.gistlist.data.dao.GistDao
import com.example.gistlist.data.dao.GistDatabase
import com.example.gistlist.data.repository.GistRepository
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.repos.GistListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val BASE_URL = "https://api.github.com/"
private const val RETROFIT_INSTANCE = "Retrofit"
private const val DATA_BASE_NAME = "favorites"

val repositoryModules = module {
    single { GistRepository(get()) }
    single { RoomRepository(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): GistDatabase {
        return Room.databaseBuilder(application, GistDatabase::class.java, DATA_BASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideFavoritesGistDao(database: GistDatabase): GistDao {
        return database.gistDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideFavoritesGistDao(get()) }
}

val netWorkModules = module {
    single(named(RETROFIT_INSTANCE)) { createNetworkClient(BASE_URL) }
    single { (get(named(RETROFIT_INSTANCE)) as Retrofit).create(RemoteGithubApi::class.java) }
}

val viewModels = module {
    viewModel {
        GistListViewModel(get(), get())
    }
   /* viewModel {
        FavoriteViewModel(get())
    }*/
}