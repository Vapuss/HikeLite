package com.vapuss.hikelite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.vapuss.hikelite.ui.navigation.NavGraph
import com.vapuss.hikelite.ui.theme.HikeLiteTheme
import com.vapuss.hikelite.viewmodel.MountainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MountainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            // Theme changes in real time via the UI switch
            HikeLiteTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}
