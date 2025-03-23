package com.jchunga.geminichat.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.jchunga.geminichat.BuildConfig
import com.jchunga.geminichat.models.ChatModel
import com.jchunga.geminichat.models.MessageModel
import com.jchunga.geminichat.room.AppDatabase
import kotlinx.coroutines.launch

class GeminiViewModel(application: Application): AndroidViewModel(application) {

    private val db  =  Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "chat_bot"
    ).build()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    private val chat by lazy {
        generativeModel.startChat()
    }

    val messageList by lazy{
        mutableStateListOf<MessageModel>()
    }

    var image by mutableStateOf<Uri>(Uri.EMPTY)

    fun sendMessage(question:String){
        viewModelScope.launch {
            try {
                messageList.add(MessageModel( question, role = "user" ))
                val contextChat = messageList.joinToString(separator = "\n") { "${it.role} : ${it.message}" }
                val response = chat.sendMessage(contextChat)
                messageList.add(MessageModel(response.text.toString(), role = "model"))
                val chatDao = db.chatDao()
                chatDao.insertChat(ChatModel( chat = question, role = "user" ))
                chatDao.insertChat(ChatModel( chat = response.text.toString(), role = "model" ))
            } catch (e:Exception){
                messageList.add( MessageModel("Error en la conversacion: ${e.message}", role = "model" ) )
            }
        }
    }

    fun loadChat(){
        try {
            viewModelScope.launch {
                val chatDao = db.chatDao()
                val savedChat = chatDao.getChat()
                messageList.clear()
                for(chat in savedChat){
                    messageList.add(MessageModel(chat.chat, role = chat.role))
                }
            }
        }catch (e:Exception){
            messageList.add( MessageModel("Error en la conversacion: ${e.message}", role = "model" ) )
        }
    }

    fun deleteChat(){
        viewModelScope.launch {
            try {
                val chatDao = db.chatDao()
                chatDao.deleteAll()
                messageList.clear()
            }catch (e:Exception){
                messageList.add( MessageModel("Error en la conversacion: ${e.message}", role = "model" ) )
            }
        }
    }

    var descriptionResponse by mutableStateOf("")
        private set

    fun descriptionImage(bitmap:Bitmap){
        viewModelScope.launch {
            try {
                val inputContent = content{
                    image(bitmap)
                    text("Describe la imagen en español y en detalle")
                }
                val response = generativeModel.generateContent(inputContent)
                descriptionResponse = response.text.toString()
            } catch (e:Exception){
                descriptionResponse = "Error al mandar la imagen: ${e.message}"
            }
        }
    }

    fun cleanVars(){
        descriptionResponse = ""
        image = Uri.EMPTY
    }

}