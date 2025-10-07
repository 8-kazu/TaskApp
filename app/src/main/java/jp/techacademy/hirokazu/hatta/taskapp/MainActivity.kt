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


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import jp.techacademy.hirokazu.hatta.taskapp.ui.screen.TaskInputScreen

//import jp.techacademy.hirokazu.hatta.taskapp.notification.TaskAlarmManager
import jp.techacademy.hirokazu.hatta.taskapp.ui.screen.TaskAlarmManager

class MainActivity : ComponentActivity() {
    // ViewModelを初期化
    private val taskViewModel: TaskViewModel by viewModels {
        val taskApp = application as TaskApp
        TaskViewModel.TaskViewModelFactory(taskApp.repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            TaskAppTheme {

                TaskListScreen(
                    viewModel = taskViewModel,
                    onAddTaskClick = {
                        // 新規タスク追加ボタンがクリックされた時の処理

                    },
                    onTaskClick = { task :Task->

                    }
                )

                // 画面の状態を管理
                var isTaskInputScreen by remember { mutableStateOf(false) }
                // 編集中のタスク
                var currentTask by remember { mutableStateOf<Task?>(null) }

// アニメーション付きの画面遷移
                AnimatedContent(
                    targetState = isTaskInputScreen,
                    transitionSpec = {
                        if (targetState) {
                            // タスク一覧画面からタスク入力画面への遷移（左へスライド）
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(durationMillis = 300)
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(durationMillis = 300)
                            )
                        } else {
                            // タスク入力画面からタスク一覧画面への遷移（右へスライド）
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(durationMillis = 300)
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(durationMillis = 300)
                            )
                        }
                    },
                    label = "Screen Transition"
                ) { isInputScreen ->
                    if (isInputScreen) {
                        // タスク入力画面
                        TaskInputScreen(
                            task = currentTask,
                            onBackClick = {
                                // 戻るボタンがクリックされたら一覧画面に戻る
                                isTaskInputScreen = false
                                // 編集完了時に編集中タスクをクリア
                                currentTask = null
                            },
                            onSaveTask = { task ->
                                // タスクを保存
                                lifecycleScope.launch {
                                    if (task.id != 0) {
                                        // 既存タスクの更新
                                        taskViewModel.updateTask(task)
                                    } else {
                                        // 新規タスクの作成
                                        taskViewModel.insertTask(task)
                                    }
                                }
                            }
                        )
                    } else {
                        // タスク一覧画面
                        TaskListScreen(
                            viewModel = taskViewModel,
                            onAddTaskClick = {
                                // 新規タスク追加ボタンがクリックされた時の処理
                                currentTask = null  // 新規タスク作成時はクリア
                                isTaskInputScreen = true
                            },
                            onTaskClick = { task ->
                                // タスク項目がクリックされた時の処理
                                currentTask = task  // 編集対象のタスクを設定
                                isTaskInputScreen = true  // タスク入力画面に遷移
                            },
                            onTaskDelete = { task ->
                                // タスク削除処理
                                lifecycleScope.launch {
                                    // データベースからタスクを削除
                                    taskViewModel.deleteTask(task)

                                    // タスクのアラームもキャンセル
                                    TaskAlarmManager.cancelTaskAlarm(applicationContext, task.id)
                                }
                            }
                        )
                    }



            }
        }
    }

    }

}

