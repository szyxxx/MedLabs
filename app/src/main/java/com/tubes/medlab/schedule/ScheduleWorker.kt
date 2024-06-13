//package com.tubes.medlab.notification
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.media3.common.util.NotificationUtil.createNotificationChannel
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import androidx.work.workDataOf
//import com.google.firebase.firestore.FirebaseFirestore
//import com.tubes.medlab.MainActivity
//import com.tubes.medlab.R
//import com.tubes.medlab.schedule.Schedule
//import com.tubes.medlab.schedule.FirebaseFirestoreUtil
//import java.util.Calendar
//import java.util.concurrent.TimeUnit
//
//class ScheduleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun doWork(): Result {
//        val scheduleId = inputData.getString("scheduleId") ?: return Result.failure()
//        val medicineName = inputData.getString("medicineName") ?: return Result.failure()
//
//        sendNotification(scheduleId, medicineName)
//        return Result.success()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun sendNotification(scheduleId: String, medicineName: String) {
//        val notificationId = scheduleId.hashCode()
//        val channelId = "medlab_notification_channel"
//
//        // Create notification channel
//        createNotificationChannel(channelId)
//
//        // Create an intent for the notification
//        val intent = Intent(applicationContext, MainActivity::class.java).apply {
//            putExtra("scheduleId", scheduleId)
//            action = "NOTIFICATION_ACTION"
//        }
//
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(
//            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // Build the notification
//        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle("Time to take your medicine")
//            .setContentText("Medicine: $medicineName")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//            ActivityCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request notification permission for Android 13 and above
//            ActivityCompat.requestPermissions(
//                applicationContext as Activity,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                REQUEST_CODE_NOTIFICATION_PERMISSION
//            )
//            return
//        }
//
//        // Show the notification
//        NotificationManagerCompat.from(applicationContext).notify(notificationId, notificationBuilder.build())
//    }
//
//    companion object {
//        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1
//    }
//
//
//    fun scheduleNotification(schedule: Schedule) {
//    val workManager = WorkManager.getInstance(applicationContext)
//
//    for (time in schedule.timeSchedule) {
//        val timeParts = time.split(":").map { it.toInt() }
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, timeParts[0])
//            set(Calendar.MINUTE, timeParts[1])
//        }
//
//        val delay = calendar.timeInMillis - System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)
//        if (delay > 0) {
//            val data = workDataOf(
//                "scheduleId" to schedule.scheduleId,
//                "medicineName" to schedule.medicineName
//            )
//
//            val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                .setInputData(data)
//                .build()
//
//            workManager.enqueue(workRequest)
//        }
//    }
//}
//
//class NotificationReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val scheduleId = intent.getStringExtra("scheduleId") ?: return
//
//        // Kurangi quantity obat dan hapus timeSchedule terkait
//        FirebaseFirestoreUtil.updateScheduleAfterNotificationClick(scheduleId)
//    }
//}