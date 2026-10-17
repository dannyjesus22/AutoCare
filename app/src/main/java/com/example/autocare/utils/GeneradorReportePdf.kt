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
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintTextoBold = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintTexto = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
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
        y += 20f

        // 2. Información del Vehículo
        canvas.drawText("Información del Vehículo", 40f, y, paintSubtitulo)
        y += 18f
        canvas.drawText("Marca / Modelo: ${vehiculo.marca} ${vehiculo.modelo}", 40f, y, paintTextoBold)
        canvas.drawText("Año: ${vehiculo.anio}", 340f, y, paintTexto)
        y += 15f
        canvas.drawText("Tipo: ${vehiculo.tipoVehiculo}", 40f, y, paintTexto)
        canvas.drawText("Kilometraje Actual: ${vehiculo.kilometraje} km", 340f, y, paintTextoBold)
        y += 18f
        canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
        y += 20f

        // 3. Tabla de Lecturas de Kilometraje
        verificarEspacio(40f)
        canvas.drawText("Historial de Lecturas de Kilometraje", 40f, y, paintSubtitulo)
        y += 18f

        if (registrosKilometraje.isEmpty()) {
            canvas.drawText("No hay registros de kilometraje guardados.", 40f, y, paintTexto)
            y += 20f
        } else {
            // Cabeceras de columna actualizadas incluyendo Recorrido
            canvas.drawText("Fecha / Hora", 40f, y, paintTextoBold)
            canvas.drawText("Km Anterior", 180f, y, paintTextoBold)
            canvas.drawText("Nuevo Km", 300f, y, paintTextoBold)
            canvas.drawText("Recorrido", 430f, y, paintTextoBold)
            y += 10f
            canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
            y += 15f

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            // Ordenamos cronológicamente para calcular el valor anterior y el recorrido
            val registrosCronologicos = registrosKilometraje.sortedBy { it.id }

            // Se imprimen en orden descendente (más reciente primero)
            registrosCronologicos.sortedByDescending { it.id }.forEach { reg ->
                verificarEspacio(20f)
                val fechaStr = formatoFecha.format(Date(reg.fecha))

                val indiceActual = registrosCronologicos.indexOf(reg)
                val regAnterior = registrosCronologicos.getOrNull(indiceActual - 1)

                val kmAnteriorTexto = if (regAnterior != null) "${regAnterior.kilometrajeActual} km" else "Inicial"
                val recorridoTexto = if (regAnterior != null) {
                    val diferencia = reg.kilometrajeActual - regAnterior.kilometrajeActual
                    if (diferencia > 0) "+$diferencia km" else "$diferencia km"
                } else {
                    "-"
                }

                canvas.drawText(fechaStr, 40f, y, paintTexto)
                canvas.drawText(kmAnteriorTexto, 180f, y, paintTexto)
                canvas.drawText("${reg.kilometrajeActual} km", 300f, y, paintTextoBold)
                canvas.drawText(recorridoTexto, 430f, y, paintTextoBold)
                y += 16f
            }
        }

        y += 10f
        verificarEspacio(20f)
        canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
        y += 20f

        // 4. Tabla de Mantenimientos
        verificarEspacio(40f)
        canvas.drawText("Historial de Mantenimientos", 40f, y, paintSubtitulo)
        y += 18f

        if (mantenimientos.isEmpty()) {
            canvas.drawText("No hay mantenimientos registrados.", 40f, y, paintTexto)
            y += 20f
        } else {
            canvas.drawText("Mantenimiento", 40f, y, paintTextoBold)
            canvas.drawText("Fecha", 200f, y, paintTextoBold)
            canvas.drawText("Km Realizado", 290f, y, paintTextoBold)
            canvas.drawText("Estado", 400f, y, paintTextoBold)
            canvas.drawText("Costo", 490f, y, paintTextoBold)
            y += 10f
            canvas.drawLine(40f, y, pageWidth - 40f, y, paintLinea)
            y += 15f

            mantenimientos.forEach { mant ->
                verificarEspacio(35f)
                canvas.drawText(mant.titulo, 40f, y, paintTextoBold)
                canvas.drawText(mant.fecha, 200f, y, paintTexto)

                val kmTexto = if (mant.completado) "${mant.kilometrajeRealizado} km" else "Pendiente"
                canvas.drawText(kmTexto, 290f, y, paintTexto)

                val estadoTexto = if (mant.completado) "REALIZADO" else "PENDIENTE"
                canvas.drawText(estadoTexto, 400f, y, paintTextoBold)
                canvas.drawText("$ ${String.format(Locale.US, "%.2f", mant.costo)}", 490f, y, paintTexto)
                y += 14f

                // Desglose limpio entre Taller y Observación
                val partesNotas = mant.notas.split(" - ", limit = 2)
                val taller = if (partesNotas.size > 1) partesNotas[0] else ""
                val observacion = if (partesNotas.size > 1) partesNotas[1] else mant.notas

                if (taller.isNotBlank()) {
                    verificarEspacio(14f)
                    canvas.drawText("  • Taller: $taller", 50f, y, paintTexto)
                    y += 14f
                }

                if (observacion.isNotBlank()) {
                    verificarEspacio(14f)
                    canvas.drawText("  • Observación: $observacion", 50f, y, paintTexto)
                    y += 14f
                }

                y += 6f
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