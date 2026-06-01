package com.vedantraut.herohub.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.herohub.presentation.home.HomeIntent
import com.vedantraut.herohub.presentation.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = {

                query = it

                println("UI_QUERY: $it")

                viewModel.onIntent(
                    HomeIntent.SearchHero(it)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Search Hero")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {

            println("UI_STATE: Loading")

            CircularProgressIndicator()
        }

        state.error?.let {

            println("UI_ERROR: $it")

            Text(text = it)
        }

        Text(
            text = "Heroes Count: ${state.heroes.size}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {

            items(state.heroes) { hero ->

                println("UI_HERO: ${hero.name}")

                Text(
                    text = hero.name,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}