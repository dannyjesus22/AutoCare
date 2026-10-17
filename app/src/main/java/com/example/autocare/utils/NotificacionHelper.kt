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
                NotificationManager.IMPORTANCE_HIGH // Alta importancia para que se muestre como banner flotante
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
        // 1. Verificar si las notificaciones están activadas en DataStore antes de enviar
        val dataStore = ConfiguracionDataStore(context)
        val notificacionesActivadas = runBlocking { dataStore.notificacionesActivadas.first() }

        if (!notificacionesActivadas) {
            return // Si el usuario desactivó las notificaciones en la configuración, no se envía nada.
        }

        // 2. Verificar permiso en Android 13+
        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // 3. Crear notificación expandible con BigTextStyle para leer el texto completo
        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            // Estilo expandible: permite desplegar la notificación para leer toda la información
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
        mensaje: String
    ): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }

            val fecha = formato.parse(fechaStr) ?: return false

            val calendario = Calendar.getInstance().apply {
                time = fecha
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
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

        // Comprueba si los recordatorios o notificaciones generales están activos en DataStore
        val notificacionesActivadas = runBlocking { dataStore.notificacionesActivadas.first() }
        val recordatoriosActivados = runBlocking { dataStore.recordatoriosActivados.first() }

        if (!notificacionesActivadas || !recordatoriosActivados) {
            return // Si están desactivados en la configuración, no ejecuta el disparo
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