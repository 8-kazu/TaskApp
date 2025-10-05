package jp.techacademy.hirokazu.hatta.taskapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val contents: String,
    val taskCategory: String,
    val date: String
)