package jp.techacademy.hirokazu.hatta.taskapp.ui.screen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import jp.techacademy.hirokazu.hatta.taskapp.data.Task
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * タスクのアラーム設定を管理するクラス
 */
object TaskAlarmManager {

    /**
     * タスクの期限日時にアラームを設定する
     * @param context コンテキスト
     * @param task 設定するタスク
     */
    fun scheduleTaskAlarm(context: Context, task: Task) {
        // AlarmManagerを取得
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // タスクの期限日時をミリ秒に変換
        val taskTimeMillis = getTaskTimeMillis(task.date)
        if (taskTimeMillis == null || taskTimeMillis <= System.currentTimeMillis()) {
            // 過去の日時の場合はアラームを設定しない
            return
        }

        // アラーム用のインテントを作成
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra("task_id", task.id)
            putExtra("title", task.title)
            putExtra("content", task.contents)
        }

        // PendingIntentを作成（タスクIDを使ってユニークにする）
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Android 12以上の場合は、setExact()を使用するためのチェック
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                // 正確なアラームを設定
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    taskTimeMillis,
                    pendingIntent
                )
            } else {
                // 正確なアラームが許可されていない場合は通常のアラームを設定
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    taskTimeMillis,
                    pendingIntent
                )
            }
        } else {
            // Android 12未満の場合は正確なアラームを設定
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                taskTimeMillis,
                pendingIntent
            )
        }
    }

    /**
     * タスクのアラームをキャンセルする
     * @param context コンテキスト
     * @param taskId キャンセルするタスクのID
     */
    fun cancelTaskAlarm(context: Context, taskId: Int) {
        // AlarmManagerを取得
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // アラーム用のインテントを作成
        val intent = Intent(context, TaskAlarmReceiver::class.java)

        // PendingIntentを作成（タスクIDを使ってユニークにする）
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntentが存在する場合はアラームをキャンセル
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    /**
     * 日時文字列をミリ秒に変換する
     * @param dateString 日時文字列（yyyy-MM-dd HH:mm形式）
     * @return 日時のミリ秒、変換失敗時はnull
     */
    private fun getTaskTimeMillis(dateString: String): Long? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time
        } catch (e: Exception) {
            null
        }
    }
}