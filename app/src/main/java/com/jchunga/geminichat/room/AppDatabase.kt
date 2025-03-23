package com.jchunga.geminichat.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jchunga.geminichat.models.ChatModel


@Database(entities = [ChatModel::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDao
}