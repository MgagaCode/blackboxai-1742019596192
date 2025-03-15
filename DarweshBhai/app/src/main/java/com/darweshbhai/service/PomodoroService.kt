package com.darweshbhai.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.darweshbhai.MainActivity
import com.darweshbhai.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class PomodoroService : Service() {

    private val binder = PomodoroBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState

    private var timerJob: Job? = null
    private var remainingTime: Long = 0
    private var sessionType: SessionType = SessionType.POMODORO

    private val NOTIFICATION_CHANNEL_ID = "pomodoro_channel"
    private val NOTIFICATION_ID = 1002

    @Inject
    lateinit var preferences: SharedPreferences

    inner class PomodoroBinder : Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows timer progress for focus sessions"
                setSound(null, null) // No sound for timer updates
                enableVibration(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startSession(type: SessionType) {
        sessionType = type
        remainingTime = when (type) {
            SessionType.POMODORO -> 25.minutes.inWholeMilliseconds
            SessionType.SHORT_BREAK -> 5.minutes.inWholeMilliseconds
            SessionType.LONG_BREAK -> 15.minutes.inWholeMilliseconds
        }
        
        startForeground(NOTIFICATION_ID, createNotification())
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            _timerState.value = TimerState.Running(remainingTime, sessionType)
            
            while (remainingTime > 0 && isActive) {
                delay(1000)
                remainingTime -= 1.seconds.inWholeMilliseconds
                _timerState.value = TimerState.Running(remainingTime, sessionType)
                updateNotification()
            }

            if (remainingTime <= 0) {
                _timerState.value = TimerState.Completed(sessionType)
                showCompletionNotification()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.Paused(remainingTime, sessionType)
        updateNotification()
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.Idle
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getSessionTitle())
            .setContentText(formatTime(remainingTime))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun showCompletionNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(
                when (sessionType) {
                    SessionType.POMODORO -> R.string.focus_session_complete
                    else -> R.string.break_time
                }
            ))
            .setContentText(
                when (sessionType) {
                    SessionType.POMODORO -> "Time for a break!"
                    else -> "Ready to focus again?"
                }
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun getSessionTitle(): String {
        return when (sessionType) {
            SessionType.POMODORO -> getString(R.string.focus_mode_title)
            SessionType.SHORT_BREAK -> getString(R.string.short_break)
            SessionType.LONG_BREAK -> getString(R.string.long_break)
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
    }

    enum class SessionType {
        POMODORO,
        SHORT_BREAK,
        LONG_BREAK
    }

    sealed class TimerState {
        object Idle : TimerState()
        data class Running(val remainingTime: Long, val sessionType: SessionType) : TimerState()
        data class Paused(val remainingTime: Long, val sessionType: SessionType) : TimerState()
        data class Completed(val sessionType: SessionType) : TimerState()
    }
}
