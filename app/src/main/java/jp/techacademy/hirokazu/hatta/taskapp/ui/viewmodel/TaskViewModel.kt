package jp.techacademy.hirokazu.hatta.taskapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import jp.techacademy.hirokazu.hatta.taskapp.data.Task
import jp.techacademy.hirokazu.hatta.taskapp.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * タスク関連の操作を行うViewModel
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    /**
     * すべてのタスクを取得するStateFlow
     */
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * タスクを挿入する
     * @param task 挿入するタスク
     */
    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    /**
     * タスクを更新する
     * @param task 更新するタスク
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    /**
     * タスクを削除する
     * @param task 削除するタスク
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    /**
     * すべてのタスクを削除する
     */
    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }

    /**
     * ViewModelのファクトリークラス
     */
    class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}