package com.example.autocare.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun enviarNotificacionMantenimiento(titulo: String, mensaje: String, notificationId: Int = 1001) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, builder.build())
    }

    // Programar la notificación para una fecha exacta (Formato DD/MM/YYYY)
    fun programarNotificacionPorFecha(fechaStr: String, titulo: String, mensaje: String) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaDeseada = sdf.parse(fechaStr) ?: return

            val calendar = Calendar.getInstance().apply {
                time = fechaDeseada
                // Se programa para las 09:00 AM del día elegido
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val tiempoMillis = calendar.timeInMillis
            val tiempoActual = System.currentTimeMillis()

            // Si la fecha elegida es HOY o una fecha pasada, enviamos la notificación 5 segundos después para probar
            val tiempoFinal = if (tiempoMillis <= tiempoActual) {
                tiempoActual + 5000 // 5 segundos después para pruebas inmediatas
            } else {
                tiempoMillis
            }

            val intent = Intent(context, NotificacionReceiver::class.java).apply {
                putExtra("titulo", titulo)
                putExtra("mensaje", mensaje)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempoFinal,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    tiempoFinal,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Receptor que captura la alarma de Android y muestra la notificación
class NotificacionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("titulo") ?: "Recordatorio de Mantenimiento"
        val mensaje = intent.getStringExtra("mensaje") ?: "Tienes un mantenimiento programado."

        val helper = NotificacionHelper(context)
        helper.enviarNotificacionMantenimiento(titulo, mensaje)
    }
}