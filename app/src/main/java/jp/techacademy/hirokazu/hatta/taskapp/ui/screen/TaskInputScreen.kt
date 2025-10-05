package jp.techacademy.hirokazu.hatta.taskapp.ui.screen



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.techacademy.hirokazu.hatta.taskapp.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
//import jp.techacademy.hirokazu.hatta.taskapp.notification.TaskAlarmManager
import kotlinx.coroutines.launch

/**
 * タスク入力画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputScreen(
    task: Task? = null,
    onBackClick: () -> Unit = {},
    onSaveTask: (Task) -> Unit = {}
) {
    // コンテキストを取得
    val context = LocalContext.current

    // Snackbarの状態を管理
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 通知権限の状態
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Android 13未満では権限不要
            }
        )
    }

    // 通知権限リクエスト用のランチャー
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("通知の権限が許可されました")
                }
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("通知の権限が拒否されました。タスク通知は表示されません。")
                }
            }
        }
    )


    // 入力値の状態管理
    var title by remember { mutableStateOf(task?.title ?: "") }
    var contents by remember { mutableStateOf(task?.contents ?: "") }
    var taskCategory by remember { mutableStateOf(task?.taskCategory ?: "") }

    // カレンダーの初期値は現在時刻から1日後
    val calendar = remember {
        Calendar.getInstance().apply {
            if (task != null) {
                // 既存タスクの日時を解析してカレンダーに設定
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val date = dateFormat.parse(task.date)
                    if (date != null) {
                        time = date
                    } else {
                        // 解析失敗時は1日後をデフォルトに
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                } catch (e: Exception) {
                    // 日付解析エラー時は1日後をデフォルトに
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            } else {
                // 新規タスク作成時は1日後をデフォルトに
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    // 日付と時刻の表示用フォーマット
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // 表示用の日付文字列
    var dateText by remember { mutableStateOf(dateFormat.format(calendar.time)) }

    // 日付選択ダイアログの表示状態
    var showDatePicker by remember { mutableStateOf(false) }

    // 時刻選択ダイアログの表示状態
    var showTimePicker by remember { mutableStateOf(false) }

    // 日付選択の状態
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis,
        initialDisplayMode = DisplayMode.Picker
    )

    // 時刻選択の状態
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )


    // Android 13以上で通知権限をリクエスト
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("タスク") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // タイトル入力フィールド
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("タイトル") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 内容入力フィールド
            OutlinedTextField(
                value = contents,
                onValueChange = { contents = it },
                label = { Text("内容") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))


            // カテゴリ入力フィールド（課題用）
            OutlinedTextField(
                value = taskCategory,
                onValueChange = { taskCategory = it },
                label = { Text("カテゴリー") },
                modifier = Modifier.fillMaxWidth(),
                //minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))


            // 日付表示
            OutlinedTextField(
                value = dateText,
                onValueChange = { },
                label = { Text("日時") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日付選択ボタン
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("日付選択")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 時刻選択ボタン
            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("時刻選択")
            }

            // 日付選択ダイアログ
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                                datePickerState.selectedDateMillis?.let { millis ->
                                    calendar.timeInMillis = millis
                                    dateText = dateFormat.format(calendar.time)
                                }
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("キャンセル")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // 時刻選択ダイアログ
            if (showTimePicker) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showTimePicker = false
                                calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                calendar.set(Calendar.MINUTE, timePickerState.minute)
                                dateText = dateFormat.format(calendar.time)
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) {
                            Text("キャンセル")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 決定ボタン
            Button(
                onClick = {
                    // 入力内容からTaskオブジェクトを作成
                    val newTask = if (task != null) {
                        // 既存タスクの更新
                        task.copy(
                            title = title,
                            contents = contents,
                            taskCategory = taskCategory,
                            date = dateText

                        )
                    } else {
                        // 新規タスクの作成
                        Task(
                            title = title,
                            contents = contents,
                            taskCategory = taskCategory,
                            date = dateText
                        )
                    }
                    // コールバックで保存処理を呼び出す
                    onSaveTask(newTask)

                    // タスクのアラームを設定
                    if (hasNotificationPermission) {
                        // 既存のタスクの場合は古いアラームをキャンセル
                        if (task != null) {
                            TaskAlarmManager.cancelTaskAlarm(context, task.id)
                        }

                        // 新しいタスクのアラームを設定
                        TaskAlarmManager.scheduleTaskAlarm(context, newTask)

                        // アラーム設定完了を通知
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("タスクの通知が設定されました")
                        }
                    } else {
                        // 通知権限がない場合は警告を表示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("通知の権限が必要です。タスク通知は表示されません。")
                        }
                    }


                    // 前の画面に戻る
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && contents.isNotBlank()
            ) {
                Text("決定")
            }
        }
    }
}