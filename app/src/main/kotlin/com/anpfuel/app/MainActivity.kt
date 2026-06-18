package com.anpfuel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anpfuel.app.ui.home.HomeScreen
import com.anpfuel.app.ui.theme.AnpFuelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnpFuelTheme {
                HomeScreen()
            }
        }
    }
}
