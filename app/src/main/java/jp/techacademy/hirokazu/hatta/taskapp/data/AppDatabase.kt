package jp.techacademy.hirokazu.hatta.taskapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * アプリケーションのデータベース
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * TaskDaoを取得する
     */
    abstract fun taskDao(): TaskDao

    companion object {
        // シングルトンインスタンス
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * データベースのインスタンスを取得する
         * @param context アプリケーションコンテキスト
         * @return データベースのインスタンス
         */
        fun getDatabase(context: Context): AppDatabase {
            // すでにインスタンスが存在する場合はそれを返す
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration() // マイグレーションに失敗した場合、データベースを再作成する
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}