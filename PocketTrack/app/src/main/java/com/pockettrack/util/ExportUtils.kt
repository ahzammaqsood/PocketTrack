package com.pockettrack.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.pockettrack.data.entity.TransactionEntity
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {
    fun writeCsv(context: Context, uri: Uri, items: List<TransactionEntity>) {
        context.contentResolver.openOutputStream(uri)?.use { os ->
            csvWriter().open(os) {
                writeRow(listOf("id", "type", "amount", "category", "note", "date"))
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                items.forEach { t ->
                    writeRow(listOf(t.id, t.type, t.amount.toString(), t.category, t.note ?: "", df.format(Date(t.date))))
                }
            }
        }
    }

    fun writePdf(context: Context, uri: Uri, items: List<TransactionEntity>) {
        val pdf = PdfDocument()
        val paint = Paint().apply { textSize = 12f }
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdf.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        var y = 40f
        canvas.drawText("PocketTrack Export", 40f, y, paint)
        y += 20
        canvas.drawText("ID | TYPE | AMOUNT | CATEGORY | NOTE | DATE", 40f, y, paint)
        y += 20
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        items.forEach { t ->
            val line = listOf(
                t.id.take(6), t.type, String.format(Locale.getDefault(),"%.2f", t.amount), t.category, (t.note ?: "").take(20), df.format(Date(t.date))
            ).joinToString(" | ")
            if (y > 800) return@forEach
            canvas.drawText(line, 40f, y, paint)
            y += 16
        }
        pdf.finishPage(page)
        context.contentResolver.openOutputStream(uri)?.use { os: OutputStream -> pdf.writeTo(os) }
        pdf.close()
    }
}