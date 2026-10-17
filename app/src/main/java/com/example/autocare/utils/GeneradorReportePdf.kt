package com.example.autocare.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.autocare.data.entities.Mantenimiento
import com.example.autocare.data.entities.RegistroKilometraje
import com.example.autocare.data.entities.Vehiculo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GeneradorReportePdf {

    fun generarYMostrarReporte(
        context: Context,
        vehiculo: Vehiculo,
        registrosKilometraje: List<RegistroKilometraje>,
        mantenimientos: List<Mantenimiento>
    ) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // Tamaño A4 (72 dpi)
        val pageHeight = 842

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paintTitulo = Paint().apply {
            color = Color.parseColor("#0D47A1")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintSubtitulo = Paint().apply {
            color = Color.parseColor("#1976D2")
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintTextoBold = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintTexto = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
        }

        val paintLinea = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 40f

        // Helper para paginación
        fun verificarEspacio(incremento: Float) {
            if (y + incremento > pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }
        }

        // 1. Encabezado
        canvas.drawText("AutoCare - Reporte de Vehículo", 40f, y, paintTitulo)
        y += 20f
        val fechaEmision = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Fecha de emisión: $fechaEmision", 40f, y, paintTexto)
        y += 15f
        canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
        y += 25f

        // 2. Información del Vehículo
        canvas.drawText("Información del Vehículo", 40f, y, paintSubtitulo)
        y += 20f
        canvas.drawText("Marca / Modelo: ${vehiculo.marca} ${vehiculo.modelo}", 40f, y, paintTextoBold)
        canvas.drawText("Año: ${vehiculo.anio}", 340f, y, paintTexto)
        y += 15f
        canvas.drawText("Tipo: ${vehiculo.tipoVehiculo}", 40f, y, paintTexto)
        canvas.drawText("Kilometraje Actual: ${vehiculo.kilometraje} km", 340f, y, paintTextoBold)
        y += 20f
        canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
        y += 25f

        // 3. Tabla de Lecturas de Kilometraje
        verificarEspacio(40f)
        canvas.drawText("Historial de Lecturas de Kilometraje", 40f, y, paintSubtitulo)
        y += 20f

        if (registrosKilometraje.isEmpty()) {
            canvas.drawText("No hay registros de kilometraje guardados.", 40f, y, paintTexto)
            y += 20f
        } else {
            canvas.drawText("Fecha / Hora", 50f, y, paintTextoBold)
            canvas.drawText("Kilometraje Registrado", 280f, y, paintTextoBold)
            y += 10f
            canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
            y += 18f

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            registrosKilometraje.sortedByDescending { it.id }.forEach { reg ->
                verificarEspacio(20f)
                val fechaStr = formatoFecha.format(Date(reg.fecha))
                canvas.drawText(fechaStr, 50f, y, paintTexto)
                canvas.drawText("${reg.kilometrajeActual} km", 280f, y, paintTexto)
                y += 18f
            }
        }

        y += 15f
        verificarEspacio(20f)
        canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
        y += 25f

        // 4. Tabla de Mantenimientos
        verificarEspacio(40f)
        canvas.drawText("Historial de Mantenimientos", 40f, y, paintSubtitulo)
        y += 20f

        if (mantenimientos.isEmpty()) {
            canvas.drawText("No hay mantenimientos registrados.", 40f, y, paintTexto)
            y += 20f
        } else {
            canvas.drawText("Título", 50f, y, paintTextoBold)
            canvas.drawText("Fecha", 200f, y, paintTextoBold)
            canvas.drawText("Km Realizado", 300f, y, paintTextoBold)
            canvas.drawText("Estado", 410f, y, paintTextoBold)
            canvas.drawText("Costo", 490f, y, paintTextoBold)
            y += 10f
            canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
            y += 18f

            mantenimientos.forEach { mant ->
                verificarEspacio(25f)
                canvas.drawText(mant.titulo, 50f, y, paintTextoBold)
                canvas.drawText(mant.fecha, 200f, y, paintTexto)
                canvas.drawText("${mant.kilometrajeRealizado} km", 300f, y, paintTexto)
                canvas.drawText(if (mant.completado) "REALIZADO" else "PENDIENTE", 410f, y, paintTexto)
                canvas.drawText("$ ${String.format(Locale.US, "%.2f", mant.costo)}", 490f, y, paintTexto)
                y += 15f

                if (mant.notas.isNotBlank()) {
                    verificarEspacio(15f)
                    canvas.drawText("Notas: ${mant.notas}", 60f, y, paintTexto)
                    y += 15f
                }
                y += 5f
            }
        }

        pdfDocument.finishPage(page)

        // 5. Guardar y Abrir mediante Intent
        try {
            val carpeta = File(context.cacheDir, "reportes")
            if (!carpeta.exists()) carpeta.mkdirs()

            val nombreLimpio = "${vehiculo.marca}_${vehiculo.modelo}".replace(" ", "_")
            val archivoPdf = File(carpeta, "Reporte_$nombreLimpio.pdf")

            pdfDocument.writeTo(FileOutputStream(archivoPdf))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                archivoPdf
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(intent, "Abrir Reporte PDF"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}