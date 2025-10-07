package jp.techacademy.hirokazu.hatta.taskapp.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button


import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel? = null,
    onAddTaskClick: () -> Unit = {},
    onTaskClick: (Task) -> Unit = {},
    onTaskDelete: (Task) -> Unit = {}
) {
    // タスクリストを取得
    val allTasks by viewModel?.allTasks?.collectAsState() ?: remember { mutableStateOf(emptyList<Task>()) }
    val filteredTasks by viewModel?.filteredTasks?.collectAsState() ?: remember { mutableStateOf(emptyList<Task>()) }

// 検索結果が空でなければfilteredTasksを、そうでなければallTasksを表示
    val tasks = if (filteredTasks.isNotEmpty()) filteredTasks else allTasks



    // 削除確認ダイアログの状態管理
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
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




        //Scaffoldが計算した余白を受け取り、その余白を考慮してUIを配置するラムダ
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp), // 上下の間隔

        ) {
          //絞り込み（課題用）


            var searchCategory by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
            ) {


                OutlinedTextField(
                    value = searchCategory,
                    onValueChange = { searchCategory = it },
                    label = { Text("カテゴリー") },
                    modifier = Modifier.weight(1f)
                )


                Button(
                    onClick = {
                        // ボタン押したらViewModelのカテゴリ絞り込みメソッドを呼ぶ
                        //viewModel?.viewModelScope?.launch {
                            viewModel?.selectTasks (searchCategory)
                        //}
                    }
                ) {
                    Text("検索")
                }
            }


                //Spacer(modifier = Modifier.width(3.dp))

            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
                .padding(top = 8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { onTaskClick(task) },
                    onLongClick = {
                        taskToDelete = task
                        showDeleteDialog = true
                    }
                )
            }
        }}
    }
// 削除確認ダイアログ
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                taskToDelete = null
            },
            title = { Text("タスクの削除") },
            text = { Text("「${taskToDelete?.title}」を削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let { onTaskDelete(it) }
                        showDeleteDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }
}




  //タスク項目のUI


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
   // title: String,
    task: Task,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick),
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
            //カテゴリ・課題用
            Text(
                text = task.category,
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




