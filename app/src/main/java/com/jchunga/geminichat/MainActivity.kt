package com.jchunga.geminichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.jchunga.geminichat.ui.theme.GeminiChatTheme
import com.jchunga.geminichat.views.HomeView
import com.jchunga.geminichat.viewModel.GeminiViewModel
import com.jchunga.geminichat.views.ModalView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel : GeminiViewModel by viewModels()
        setContent {
            GeminiChatTheme {
                HomeView(viewModel = viewModel)
//                ModalView()
            }
        }
    }
}