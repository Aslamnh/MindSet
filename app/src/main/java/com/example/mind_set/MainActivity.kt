package com.example.mind_set

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mind_set.ui.navigation.MindSetNavGraph
import com.example.mind_set.ui.theme.MindSetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindSetTheme {
                MindSetNavGraph()
            }
        }
    }
}
