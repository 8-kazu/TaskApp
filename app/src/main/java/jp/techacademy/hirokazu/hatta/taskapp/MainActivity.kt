package jp.techacademy.hirokazu.hatta.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.techacademy.hirokazu.hatta.taskapp.ui.theme.TaskAppTheme


import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import jp.techacademy.hirokazu.hatta.taskapp.ui.screen.TaskListScreen
import jp.techacademy.hirokazu.hatta.taskapp.ui.theme.TaskAppTheme

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import jp.techacademy.hirokazu.hatta.taskapp.data.Task
import jp.techacademy.hirokazu.hatta.taskapp.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    // ViewModelを初期化
    private val taskViewModel: TaskViewModel by viewModels {
        val taskApp = application as TaskApp
        TaskViewModel.TaskViewModelFactory(taskApp.repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // アプリ起動時にデータベースを初期化し、サンプルデータを登録
        initializeDatabase()

        setContent {
            TaskAppTheme {

                TaskListScreen(
                    viewModel = taskViewModel,
                    onAddTaskClick = {
                        // 新規タスク追加ボタンがクリックされた時の処理
                        // 後ほど実装予定
                        Toast.makeText(this, "新規タスク追加", Toast.LENGTH_SHORT).show()
                    },
                    onTaskClick = { task :Task->
                        // タスク項目がクリックされた時の処理
                        // 後ほど実装予定
                        Toast.makeText(this, "タスク: ${task.title} がクリックされました", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }



    /**
     * データベースを初期化し、サンプルデータを登録する
     */
    private fun initializeDatabase() {
        lifecycleScope.launch {
            // 既存のデータをすべて削除
            taskViewModel.deleteAllTasks()

            // サンプルデータを登録
            val sampleTask = Task(
                title = "作業",
                contents = "プログラムを書いてPUSHする",
                date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )
            taskViewModel.insertTask(sampleTask)
        }
    }

}

