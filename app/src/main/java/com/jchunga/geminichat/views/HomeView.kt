package com.jchunga.geminichat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jchunga.geminichat.components.GlobeMessage
import com.jchunga.geminichat.components.MessageInput
import com.jchunga.geminichat.components.Title
import com.jchunga.geminichat.ui.theme.backColor
import com.jchunga.geminichat.viewModel.GeminiViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: GeminiViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Title() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backColor
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.deleteChat()
                        }

                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.White)
                    }
                }
            )
        }
    ) { pad ->

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(pad)
                .background(backColor)
        ) {
            var showModal by remember {
                mutableStateOf(false)
            }

            ChatContent(modifier = Modifier.weight(1f), viewModel = viewModel)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showModal = true }
                ) {
                    Icon(imageVector = Icons.Filled.Camera, contentDescription = "Camera")
                }
                MessageInput(
                    onClick = {
                        viewModel.sendMessage(it)
                    }
                )
            }

            ModalView(
                showModal = showModal,
                onDismiss = {
                    showModal = false
                    viewModel.cleanVars()
                },
                viewModel = viewModel
            )

        }

    }

}

@Composable
fun ChatContent(modifier: Modifier = Modifier, viewModel: GeminiViewModel) {

    LaunchedEffect(Unit) {
        viewModel.loadChat()
    }

    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(viewModel.messageList.reversed()) {
            GlobeMessage(messageModel = it)
        }

    }

}