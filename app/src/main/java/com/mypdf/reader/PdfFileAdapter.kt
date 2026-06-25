package com.mypdf.reader

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PdfFileAdapter(
    private val files: List<PdfFile>,
    private val isReadingList: Boolean = false,
    private val onOpenFile: (PdfFile) -> Unit,
    private val onAddToList: ((PdfFile) -> Unit)? = null,
    private val onMoveUp: ((Int) -> Unit)? = null,
    private val onMoveDown: ((Int) -> Unit)? = null,
    private val onMoveTo: ((Int, Int) -> Unit)? = null
) : RecyclerView.Adapter<PdfFileAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvFileName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnOpenFile: TextView = view.findViewById(R.id.btnOpenFile)
        val btnAddToList: TextView = view.findViewById(R.id.btnAddToList)
        val layoutControls: View = view.findViewById(R.id.layoutControls)
        val btnOpenReading: TextView = view.findViewById(R.id.btnOpenReading)
        val btnMoveUp: TextView = view.findViewById(R.id.btnMoveUp)
        val btnMoveDown: TextView = view.findViewById(R.id.btnMoveDown)
        val btnMoveTo: TextView = view.findViewById(R.id.btnMoveTo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        holder.tvName.text = "${file.name}.pdf"

        if (isReadingList) {
            // Reading list mode
            holder.tvName.setTextColor(
                if (file.isRead) Color.parseColor("#999999") else Color.parseColor("#212121")
            )
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = if (file.isRead) "✓ Đã đọc" else "Chưa đọc"
            holder.tvStatus.setTextColor(
                if (file.isRead) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
            )
            holder.layoutControls.visibility = View.VISIBLE
            holder.btnOpenFile.visibility = View.GONE
            holder.btnAddToList.visibility = View.GONE

            holder.btnMoveUp.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            holder.btnMoveDown.visibility = if (position < files.size - 1) View.VISIBLE else View.INVISIBLE

            holder.btnOpenReading.setOnClickListener { onOpenFile(file) }
            holder.btnMoveUp.setOnClickListener { onMoveUp?.invoke(position) }
            holder.btnMoveDown.setOnClickListener { onMoveDown?.invoke(position) }

            holder.btnMoveTo.setOnClickListener {
                val context = holder.itemView.context
                val input = EditText(context).apply {
                    hint = "Nhập vị trí (1 - ${files.size})"
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    setPadding(40, 20, 40, 20)
                }
                AlertDialog.Builder(context)
                    .setTitle("Chuyển đến vị trí")
                    .setMessage("Vị trí hiện tại: ${position + 1}")
                    .setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        val target = input.text.toString().toIntOrNull()
                        if (target != null && target in 1..files.size) {
                            onMoveTo?.invoke(position, target - 1)
                        }
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }

        } else {
            // All files mode
            holder.tvName.setTextColor(Color.parseColor("#212121"))
            holder.tvStatus.visibility = View.GONE
            holder.layoutControls.visibility = View.GONE
            holder.btnOpenFile.visibility = View.VISIBLE
            holder.btnAddToList.visibility = View.VISIBLE

            holder.btnOpenFile.setOnClickListener { onOpenFile(file) }
            holder.btnAddToList.setOnClickListener { onAddToList?.invoke(file) }
        }
    }

    override fun getItemCount() = files.size
}
