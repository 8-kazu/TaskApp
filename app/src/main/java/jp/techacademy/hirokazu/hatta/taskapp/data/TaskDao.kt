package jp.techacademy.hirokazu.hatta.taskapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow




@Dao
interface TaskDao {
    /**
     * すべてのタスクを日付の昇順で取得する
     * @return タスクのリストを含むFlow
     */
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasks(): Flow<List<Task>>
//非同期データの流れ（ストリーム）
    //一度だけ返す suspend fun と違い、値の変化を継続的に監視して受け取れる

    /**
     * 指定されたIDのタスクを取得する
     * @param id タスクID
     * @return 指定されたIDのタスク
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?
//Task? は 見つからなければ null になることを示す
    //意味: 引数 id のタスクを非同期で取得

    /**
     * 新しいタスクを挿入する
     * @param task 挿入するタスク
     * @return 挿入されたタスクのID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    //IDが同じタスクが存在すれば置き換える
//suspend なので非同期で呼び出せます。
    //戻り値 Long は 挿入された行のID（主キー）

    /**
     * タスクを更新する
     * @param task 更新するタスク
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * タスクを削除する
     * @param task 削除するタスク
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * すべてのタスクを削除する
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}