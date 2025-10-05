package jp.techacademy.hirokazu.hatta.taskapp.ui.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import jp.techacademy.hirokazu.hatta.taskapp.MainActivity
import jp.techacademy.hirokazu.hatta.taskapp.R

/**
 * タスク通知用のBroadcastReceiver
 */
class TaskAlarmReceiver : BroadcastReceiver() {

    companion object {
        // 通知チャンネルID
        private const val CHANNEL_ID = "task_notification_channel"
        // 通知のリクエストコード
        private const val NOTIFICATION_REQUEST_CODE = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        // インテントからタスク情報を取得
        val taskId = intent.getIntExtra("task_id", -1)
        val title = intent.getStringExtra("title") ?: "タスク"
        val content = intent.getStringExtra("content") ?: "タスクの時間です"

        // 通知を表示
        showNotification(context, taskId, title, content)
    }

    /**
     * 通知を表示する
     */
    private fun showNotification(context: Context, taskId: Int, title: String, content: String) {
        // NotificationManagerを取得
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0以上の場合は通知チャンネルを作成
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知チャンネルの重要度を設定
            val importance = NotificationManager.IMPORTANCE_HIGH
            // 通知チャンネルを作成
            val channel = NotificationChannel(CHANNEL_ID, "タスク通知", importance).apply {
                description = "タスクの期限を通知します"
                enableVibration(true)
            }
            // 通知チャンネルをシステムに登録
            notificationManager.createNotificationChannel(channel)
        }

        // 通知タップ時のインテントを作成
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId)
        }

        // PendingIntentを作成
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 通知を構築
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_dialog_info))
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // 通知を表示
        notificationManager.notify(taskId, notification)
    }
}