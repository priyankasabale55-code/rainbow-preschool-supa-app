package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PreschoolDatabase
import com.example.data.PreschoolRepository
import com.example.ui.PreschoolViewModel
import com.example.ui.RainbowPreschoolApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PreschoolDatabase.getDatabase(applicationContext)
        val repository = PreschoolRepository(database.preschoolDao())

        setContent {
            val preschoolViewModel: PreschoolViewModel = viewModel(
                factory = PreschoolViewModel.provideFactory(repository, applicationContext)
            )
            val uiState by preschoolViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                RainbowPreschoolApp(viewModel = preschoolViewModel)
            }
        }
    }
}
