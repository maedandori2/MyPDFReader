package com.mypdf.reader

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PdfFileAdapter(
    private val files: List<PdfFile>,
    private val onItemClick: (PdfFile) -> Unit,
    private val onItemLongClick: (PdfFile) -> Unit,
    private val isReadingList: Boolean = false,
    private val onMoveUp: ((Int) -> Unit)? = null,
    private val onMoveDown: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<PdfFileAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvFileName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnUp: ImageButton = view.findViewById(R.id.btnMoveUp)
        val btnDown: ImageButton = view.findViewById(R.id.btnMoveDown)
        val layoutControls: View = view.findViewById(R.id.layoutControls)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        holder.tvName.text = file.name
        holder.tvName.setTextColor(
            if (file.isRead && isReadingList) Color.parseColor("#999999")
            else Color.parseColor("#212121")
        )

        if (isReadingList) {
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = if (file.isRead) "✓ Đã đọc" else "Chưa đọc"
            holder.tvStatus.setTextColor(
                if (file.isRead) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
            )
            holder.layoutControls.visibility = View.VISIBLE
            holder.btnUp.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            holder.btnDown.visibility = if (position < files.size - 1) View.VISIBLE else View.INVISIBLE
            holder.btnUp.setOnClickListener { onMoveUp?.invoke(position) }
            holder.btnDown.setOnClickListener { onMoveDown?.invoke(position) }
        } else {
            holder.tvStatus.visibility = View.GONE
            holder.layoutControls.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(file) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(file)
            true
        }
    }

    override fun getItemCount() = files.size
}
