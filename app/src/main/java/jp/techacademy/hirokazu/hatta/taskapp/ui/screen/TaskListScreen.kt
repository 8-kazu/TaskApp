package jp.techacademy.hirokazu.hatta.taskapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.techacademy.hirokazu.hatta.taskapp.ui.theme.TaskAppTheme


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import jp.techacademy.hirokazu.hatta.taskapp.data.Task
import jp.techacademy.hirokazu.hatta.taskapp.ui.viewmodel.TaskViewModel

class TaskListScreen {
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel? = null,
    onAddTaskClick: () -> Unit = {},
    onTaskClick: (Task) -> Unit = {}
) {
    // サンプルタスクリスト
    //val sampleTasks = listOf("aaa", "bbb", "ccc")

    // タスクリストを取得
    val tasks by viewModel?.allTasks?.collectAsState() ?: run { androidx.compose.runtime.remember { mutableStateOf(emptyList<Task>()) }}
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskApp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },



        //外側の { } はScaffoldへの「配置指示」
        //
        //内側の { } はボタン自体の中身
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        //innerPadding ->
        //
        //Scaffoldが計算した余白を受け取り、その余白を考慮してUIを配置するラムダ
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
}

/**
 * タスク項目のUI
 */
@Composable
fun TaskItem(
   // title: String,
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = task.contents,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                text = task.date,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    TaskAppTheme {
        // プレビュー用のダミーデータ
        val dummyTask = Task(id = 1, title = "作業", contents = "プログラムを書いてPUSHする", date = "2025-03-05 12:00")
        TaskListScreen()
    }
}

//    ksp(libs.androidx.room.compiler)