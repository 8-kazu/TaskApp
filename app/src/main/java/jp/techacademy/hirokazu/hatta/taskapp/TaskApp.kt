package jp.techacademy.hirokazu.hatta.taskapp

import android.app.Application
import jp.techacademy.hirokazu.hatta.taskapp.data.AppDatabase
import jp.techacademy.hirokazu.hatta.taskapp.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * アプリケーションクラス
 */
class TaskApp : Application() {
    // アプリケーションスコープのコルーチン
    private val applicationScope = CoroutineScope(SupervisorJob())

    // データベースとリポジトリの遅延初期化
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}