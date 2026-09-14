package com.vedantraut.herohub.di

import com.vedantraut.herohub.core.network.NetworkConstants
import com.vedantraut.herohub.data.remote.api.HeroApi
import com.vedantraut.herohub.data.repository.FavoritesRepositoryImpl
import com.vedantraut.herohub.data.repository.HeroRepositoryImpl
import com.vedantraut.herohub.data.repository.SettingsRepositoryImpl
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import com.vedantraut.herohub.domain.repository.HeroRepository
import com.vedantraut.herohub.domain.repository.SettingsRepository
import com.vedantraut.herohub.domain.usecase.GetAllHeroesUseCase
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import com.vedantraut.herohub.domain.usecase.SearchHeroesUseCase
import com.vedantraut.herohub.presentation.categories.CategoriesViewModel
import com.vedantraut.herohub.presentation.favorites.FavoritesViewModel
import com.vedantraut.herohub.presentation.home.HomeViewModel
import com.vedantraut.herohub.presentation.search.SearchViewModel
import com.vedantraut.herohub.presentation.settings.SettingsViewModel
import com.vedantraut.herohub.domain.battle.BattleEngine
import com.vedantraut.herohub.domain.usecase.SimulateBattleUseCase
import com.vedantraut.herohub.presentation.battle.BattleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import androidx.room.Room
import com.vedantraut.herohub.data.local.database.HeroDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HeroApi::class.java)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            HeroDatabase::class.java,
            "herohub.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    single {
        get<HeroDatabase>().heroDao()
    }

    single<HeroRepository> {
        HeroRepositoryImpl(api = get(), heroDao = get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    single {
        SearchHeroesUseCase(get())
    }

    single {
        GetHomeHeroesUseCase(get())
    }

    single {
        GetAllHeroesUseCase(get())
    }

    single {
        BattleEngine()
    }

    single {
        SimulateBattleUseCase(get())
    }

    viewModel {
        HomeViewModel(
            getHomeHeroesUseCase = get(),
            searchHeroesUseCase = get(),
            favoritesRepository = get()
        )
    }

    viewModel {
        BattleViewModel(
            getHomeHeroesUseCase = get(),
            simulateBattleUseCase = get()
        )
    }

    viewModel {
        CategoriesViewModel(
            getHomeHeroesUseCase = get(),
            getAllHeroesUseCase = get(),
            favoritesRepository = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchHeroesUseCase = get(),
            getHomeHeroesUseCase = get(),
            favoritesRepository = get()
        )
    }

    viewModel {
        FavoritesViewModel(
            favoritesRepository = get(),
            getHomeHeroesUseCase = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsRepository = get()
        )
    }
}