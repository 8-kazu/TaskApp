package jp.techacademy.hirokazu.hatta.taskapp.data

import kotlinx.coroutines.flow.Flow

/**
 * タスクデータの操作を行うリポジトリ
 */
class TaskRepository(private val taskDao: TaskDao) {
    /**
     * すべてのタスクを取得する
     */
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    /**
     * 指定されたIDのタスクを取得する
     * @param id タスクID
     */
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }

    /**
     * タスクを挿入する
     * @param task 挿入するタスク
     */
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    /**
     * タスクを更新する
     * @param task 更新するタスク
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    /**
     * タスクを削除する
     * @param task 削除するタスク
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * すべてのタスクを削除する
     */
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

//タスク絞り込み（課題用）
    suspend fun selectTasks(taskCategory: String): List<Task>{
        return taskDao.selectTasks(taskCategory)
    }
}