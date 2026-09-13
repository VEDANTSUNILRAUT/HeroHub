package com.vedantraut.herohub.di

import com.vedantraut.herohub.core.network.NetworkConstants
import com.vedantraut.herohub.data.remote.api.HeroApi
import com.vedantraut.herohub.data.repository.FavoritesRepositoryImpl
import com.vedantraut.herohub.data.repository.HeroRepositoryImpl
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import com.vedantraut.herohub.domain.repository.HeroRepository
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import com.vedantraut.herohub.domain.usecase.SearchHeroesUseCase
import com.vedantraut.herohub.presentation.favorites.FavoritesViewModel
import com.vedantraut.herohub.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
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

    single<HeroRepository> {
        HeroRepositoryImpl(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(androidContext())
    }

    single {
        SearchHeroesUseCase(get())
    }

    single {
        GetHomeHeroesUseCase(get())
    }

    viewModel {
        HomeViewModel(
            getHomeHeroesUseCase = get(),
            searchHeroesUseCase = get(),
            favoritesRepository = get()
        )
    }

    viewModel {
        FavoritesViewModel(
            favoritesRepository = get(),
            getHomeHeroesUseCase = get()
        )
    }
}