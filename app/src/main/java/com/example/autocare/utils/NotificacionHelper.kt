package com.example.autocare.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.autocare.data.ConfiguracionDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificacionHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "canal_mantenimiento_autocare"
        const val CHANNEL_NAME = "Recordatorios de Mantenimiento"
        const val CHANNEL_DESC = "Notificaciones sobre próximos mantenimientos de tu vehículo"
    }

    init {
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    fun enviarNotificacionMantenimiento(
        titulo: String,
        mensaje: String,
        notificationId: Int = 1001
    ) {
        val dataStore = ConfiguracionDataStore(context)
        val notificacionesActivadas = runBlocking { dataStore.notificacionesActivadas.first() }

        if (!notificacionesActivadas) {
            return
        }

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(mensaje)
                    .setBigContentTitle(titulo)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notificacion)
    }

    fun programarNotificacionPorFecha(
        fechaStr: String,
        titulo: String,
        mensaje: String,
        hora: Int = 9,
        minuto: Int = 0
    ): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }

            val fecha = formato.parse(fechaStr) ?: return false

            val calendario = Calendar.getInstance().apply {
                time = fecha
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val cuando = calendario.timeInMillis

            if (cuando <= System.currentTimeMillis()) {
                return false
            }

            val intent = Intent(context, NotificacionReceiver::class.java).apply {
                putExtra("titulo", titulo)
                putExtra("mensaje", mensaje)
            }

            val id = (System.currentTimeMillis() and 0x7FFFFFFF).toInt()

            val pendiente = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmas = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmas.canScheduleExactAlarms()
                ) {
                    alarmas.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cuando, pendiente)
                } else {
                    alarmas.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cuando, pendiente)
                }
            } else {
                alarmas.set(AlarmManager.RTC_WAKEUP, cuando, pendiente)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class NotificacionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dataStore = ConfiguracionDataStore(context)
        val notificacionesActivadas = runBlocking { dataStore.notificacionesActivadas.first() }
        val recordatoriosActivados = runBlocking { dataStore.recordatoriosActivados.first() }

        if (!notificacionesActivadas || !recordatoriosActivados) {
            return
        }

        val titulo = intent.getStringExtra("titulo") ?: "Recordatorio de Mantenimiento"
        val mensaje = intent.getStringExtra("mensaje") ?: "Tienes un mantenimiento programado."

        NotificacionHelper(context).enviarNotificacionMantenimiento(
            titulo = titulo,
            mensaje = mensaje,
            notificationId = (System.currentTimeMillis() and 0x7FFFFFFF).toInt()
        )
    }
}