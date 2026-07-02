package com.mypdf.reader

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PdfFileAdapter(
    private val files: List<PdfFile>,
    private val isReadingList: Boolean = false,
    private val onOpenFile: (PdfFile) -> Unit,
    private val onAddToList: ((PdfFile) -> Unit)? = null,
    private val onMoveUp: ((Int) -> Unit)? = null,
    private val onMoveDown: ((Int) -> Unit)? = null,
    private val onRemove: ((Int) -> Unit)? = null,
    private val onSwapPosition: ((Int, Int) -> Unit)? = null
) : RecyclerView.Adapter<PdfFileAdapter.ViewHolder>() {

    private var adapterScope: CoroutineScope? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        adapterScope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        adapterScope?.cancel()
        adapterScope = null
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: EditText = view.findViewById(R.id.tvIndex)
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val tvName: TextView = view.findViewById(R.id.tvFileName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvMetadata: TextView = view.findViewById(R.id.tvMetadata)
        val btnOpenFile: TextView = view.findViewById(R.id.btnOpenFile)
        val btnAddToList: TextView = view.findViewById(R.id.btnAddToList)
        val layoutControls: View = view.findViewById(R.id.layoutControls)
        val btnOpenReading: TextView = view.findViewById(R.id.btnOpenReading)
        val btnMoveUp: TextView = view.findViewById(R.id.btnMoveUp)
        val btnMoveDown: TextView = view.findViewById(R.id.btnMoveDown)
        val btnRemove: TextView = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.tvName.text = "${file.name}.pdf"

        // Hiển thị metadata (品名, 自社品番, 自社品名) cho cả 2 tab
        val metadataText = PdfMetadataManager.formatForDisplay("${file.name}.pdf")
        if (metadataText != null) {
            holder.tvMetadata.text = metadataText
            holder.tvMetadata.visibility = View.VISIBLE
        } else {
            holder.tvMetadata.visibility = View.GONE
        }

        if (isReadingList) {
            // Số thứ tự thay thumbnail - có thể sửa trực tiếp
            holder.tvIndex.visibility = View.VISIBLE
            holder.ivThumbnail.visibility = View.GONE
            val expectedText = "${position + 1}"
            if (holder.tvIndex.text.toString() != expectedText) {
                holder.tvIndex.setText(expectedText)
            }

            // Tên file lớn hơn trong reading list (cỡ chữ tuỳ chọn)
            holder.tvName.textSize = SettingsManager.getFileNameSize().toFloat()

            holder.tvName.setTextColor(
                if (file.isRead) Color.parseColor("#999999") else Color.parseColor("#212121")
            )
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = if (file.isRead) LocaleHelper.getString("status_read") else LocaleHelper.getString("status_unread")
            holder.tvStatus.setTextColor(
                if (file.isRead) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
            )

            holder.layoutControls.visibility = View.VISIBLE
            holder.btnOpenFile.visibility = View.GONE
            holder.btnAddToList.visibility = View.GONE

            holder.btnMoveUp.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            holder.btnMoveDown.visibility = if (position < files.size - 1) View.VISIBLE else View.INVISIBLE

            holder.btnOpenReading.setOnClickListener { onOpenFile(file) }
            holder.btnMoveUp.setOnClickListener { onMoveUp?.invoke(holder.adapterPosition) }
            holder.btnMoveDown.setOnClickListener { onMoveDown?.invoke(holder.adapterPosition) }
            holder.btnRemove.setOnClickListener { onRemove?.invoke(holder.adapterPosition) }

            // Xử lý sửa số thứ tự trực tiếp → swap vị trí
            setupIndexEditor(holder)

        } else {
            holder.tvIndex.visibility = View.GONE
            holder.ivThumbnail.visibility = View.VISIBLE
            
            // Khôi phục text size mặc định cho tab All
            holder.tvName.textSize = 15f

            // Xử lý load thumbnail
            holder.ivThumbnail.tag = file.path
            holder.ivThumbnail.setImageBitmap(null)
            holder.ivThumbnail.setBackgroundColor(Color.parseColor("#E0E0E0"))

            adapterScope?.launch {
                val bitmap = PdfThumbnailLoader.loadThumbnail(file.path, 120, 160)
                if (holder.ivThumbnail.tag == file.path) {
                    if (bitmap != null) {
                        holder.ivThumbnail.setImageBitmap(bitmap)
                        holder.ivThumbnail.setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        // Lỗi load ảnh, đổi màu báo lỗi
                        holder.ivThumbnail.setBackgroundColor(Color.parseColor("#FFCDD2"))
                    }
                }
            }

            holder.tvName.setTextColor(Color.parseColor("#212121"))
            holder.tvStatus.visibility = View.GONE
            holder.layoutControls.visibility = View.GONE
            holder.btnOpenFile.visibility = View.VISIBLE
            holder.btnAddToList.visibility = View.VISIBLE

            holder.btnOpenFile.setOnClickListener { onOpenFile(file) }
            holder.btnAddToList.setOnClickListener { onAddToList?.invoke(file) }
        }
    }

    /**
     * Xử lý khi người dùng sửa số thứ tự trong ô EditText.
     * Ví dụ: item đang ở vị trí 1, người dùng sửa thành 3 → item 1 và item 3 hoán đổi vị trí.
     */
    private fun setupIndexEditor(holder: ViewHolder) {
        // Xử lý khi nhấn Done trên bàn phím
        holder.tvIndex.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                holder.tvIndex.clearFocus()
                true
            } else {
                false
            }
        }

        // Xử lý khi mất focus (bấm ra ngoài)
        holder.tvIndex.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                handleIndexChange(holder)
            }
        }
    }

    /**
     * Đọc số mới từ EditText, nếu khác vị trí hiện tại thì gọi callback swap.
     */
    private fun handleIndexChange(holder: ViewHolder) {
        val currentPos = holder.adapterPosition
        if (currentPos == RecyclerView.NO_POSITION) return

        val inputText = holder.tvIndex.text.toString().trim()
        val newIndex = inputText.toIntOrNull() ?: return

        // Chuyển từ số hiển thị (1-based) sang vị trí mảng (0-based)
        val targetPos = newIndex - 1

        if (targetPos != currentPos && targetPos in 0 until files.size) {
            onSwapPosition?.invoke(currentPos, targetPos)
        } else {
            // Số không hợp lệ hoặc không thay đổi → khôi phục lại số cũ
            val expectedText = "${currentPos + 1}"
            if (inputText != expectedText) {
                holder.tvIndex.setText(expectedText)
            }
        }
    }

    override fun getItemCount() = files.size
}

