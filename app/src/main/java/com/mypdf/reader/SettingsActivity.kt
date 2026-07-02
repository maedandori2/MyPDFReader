package com.mypdf.reader

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mypdf.reader.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var currentDownloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.init(this)
        SettingsManager.init(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvSettingsTitle.text = LocaleHelper.getString("settings_title")
        binding.tvUpdateSectionTitle.text = LocaleHelper.getString("update_section")
        val currentVersion = packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvCurrentVersion.text = LocaleHelper.getString("current_version").replace("%s", currentVersion)
        binding.btnCheckUpdate.text = LocaleHelper.getString("check_update")
        binding.btnDownloadUpdate.text = LocaleHelper.getString("download")
        binding.tvReadingSectionTitle.text = LocaleHelper.getString("reading_section")
        binding.switchKeepScreenOn.text = LocaleHelper.getString("keep_screen_on")
        binding.switchKeepScreenOn.isChecked = SettingsManager.isKeepScreenOn()

        binding.tvMetadataColorTitle.text = LocaleHelper.getString("metadata_colors_title")
        binding.tvMetadataColorDesc.text = LocaleHelper.getString("metadata_colors_desc")
        updateColorButtons()
    }

    private fun updateColorButtons() {
        val labelStr = LocaleHelper.getString("label_color")
        val valueStr = LocaleHelper.getString("value_color")

        setColorButtonAppearance(binding.btnLabelJishaHinban, SettingsManager.getMetadataLabelColor("自社品番"), labelStr)
        setColorButtonAppearance(binding.btnValueJishaHinban, SettingsManager.getMetadataValueColor("自社品番"), valueStr)

        setColorButtonAppearance(binding.btnLabelHinban, SettingsManager.getMetadataLabelColor("品番"), labelStr)
        setColorButtonAppearance(binding.btnValueHinban, SettingsManager.getMetadataValueColor("品番"), valueStr)

        setColorButtonAppearance(binding.btnLabelJishaHinmei, SettingsManager.getMetadataLabelColor("自社品名"), labelStr)
        setColorButtonAppearance(binding.btnValueJishaHinmei, SettingsManager.getMetadataValueColor("自社品名"), valueStr)

        setColorButtonAppearance(binding.btnLabelHinmei, SettingsManager.getMetadataLabelColor("品名"), labelStr)
        setColorButtonAppearance(binding.btnValueHinmei, SettingsManager.getMetadataValueColor("品名"), valueStr)
    }

    private fun setColorButtonAppearance(button: android.widget.TextView, hexColor: String, labelText: String) {
        val color = try { android.graphics.Color.parseColor(hexColor) } catch (_: Exception) { android.graphics.Color.parseColor("#78909C") }
        val drawable = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(color)
            setStroke(2, android.graphics.Color.parseColor("#CCCCCC"))
        }
        button.background = drawable
        button.text = "$labelText ($hexColor)"
        val brightness = (android.graphics.Color.red(color) * 299 + android.graphics.Color.green(color) * 587 + android.graphics.Color.blue(color) * 114) / 1000
        button.setTextColor(if (brightness > 150) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.switchKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setKeepScreenOn(isChecked)
        }

        binding.btnLabelJishaHinban.setOnClickListener {
            showColorPickerDialog("自社品番 - ${LocaleHelper.getString("label_color")}", SettingsManager.getMetadataLabelColor("自社品番")) { hex ->
                SettingsManager.setMetadataLabelColor("自社品番", hex)
                updateColorButtons()
            }
        }
        binding.btnValueJishaHinban.setOnClickListener {
            showColorPickerDialog("自社品番 - ${LocaleHelper.getString("value_color")}", SettingsManager.getMetadataValueColor("自社品番")) { hex ->
                SettingsManager.setMetadataValueColor("自社品番", hex)
                updateColorButtons()
            }
        }

        binding.btnLabelHinban.setOnClickListener {
            showColorPickerDialog("品番 - ${LocaleHelper.getString("label_color")}", SettingsManager.getMetadataLabelColor("品番")) { hex ->
                SettingsManager.setMetadataLabelColor("品番", hex)
                updateColorButtons()
            }
        }
        binding.btnValueHinban.setOnClickListener {
            showColorPickerDialog("品番 - ${LocaleHelper.getString("value_color")}", SettingsManager.getMetadataValueColor("品番")) { hex ->
                SettingsManager.setMetadataValueColor("品番", hex)
                updateColorButtons()
            }
        }

        binding.btnLabelJishaHinmei.setOnClickListener {
            showColorPickerDialog("自社品名 - ${LocaleHelper.getString("label_color")}", SettingsManager.getMetadataLabelColor("自社品名")) { hex ->
                SettingsManager.setMetadataLabelColor("自社品名", hex)
                updateColorButtons()
            }
        }
        binding.btnValueJishaHinmei.setOnClickListener {
            showColorPickerDialog("自社品名 - ${LocaleHelper.getString("value_color")}", SettingsManager.getMetadataValueColor("自社品名")) { hex ->
                SettingsManager.setMetadataValueColor("自社品名", hex)
                updateColorButtons()
            }
        }

        binding.btnLabelHinmei.setOnClickListener {
            showColorPickerDialog("品名 - ${LocaleHelper.getString("label_color")}", SettingsManager.getMetadataLabelColor("品名")) { hex ->
                SettingsManager.setMetadataLabelColor("品名", hex)
                updateColorButtons()
            }
        }
        binding.btnValueHinmei.setOnClickListener {
            showColorPickerDialog("品名 - ${LocaleHelper.getString("value_color")}", SettingsManager.getMetadataValueColor("品名")) { hex ->
                SettingsManager.setMetadataValueColor("品名", hex)
                updateColorButtons()
            }
        }

        binding.btnCheckUpdate.setOnClickListener {
            checkUpdate()
        }

        binding.btnDownloadUpdate.setOnClickListener {
            currentDownloadUrl?.let { url ->
                startDownload(url)
            }
        }
    }

    private fun showColorPickerDialog(title: String, currentHex: String, onColorSelected: (String) -> Unit) {
        val options = arrayOf(
            "🎨 ${LocaleHelper.getString("custom_hex_title")}...",
            "🔘 Xám nhạt (#78909C)",
            "🔴 Đỏ thẫm (#C62828)",
            "🔵 Xanh dương đậm (#0D47A1)",
            "🟢 Xanh lá thẫm (#2E7D32)",
            "🟠 Cam rực rỡ (#E65100)",
            "🟣 Tím than (#4A148C)",
            "🟤 Nâu đất (#4E342E)",
            "⚫ Đen (#212121)",
            "🔘 Xám ghi (#455A64)",
            "🔵 Xanh ngọc (#00838F)"
        )
        val hexes = arrayOf(
            "CUSTOM",
            "#78909C",
            "#C62828",
            "#0D47A1",
            "#2E7D32",
            "#E65100",
            "#4A148C",
            "#4E342E",
            "#212121",
            "#455A64",
            "#00838F"
        )

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(options) { _, which ->
                if (which == 0) {
                    showCustomHexDialog(currentHex, onColorSelected)
                } else {
                    onColorSelected(hexes[which])
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showCustomHexDialog(currentHex: String, onColorSelected: (String) -> Unit) {
        val input = android.widget.EditText(this).apply {
            setText(currentHex)
            setSelection(currentHex.length)
            setPadding(40, 30, 40, 30)
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(LocaleHelper.getString("custom_hex_title"))
            .setMessage(LocaleHelper.getString("custom_hex_desc"))
            .setView(input)
            .setPositiveButton("Đồng ý") { _, _ ->
                var hex = input.text.toString().trim()
                if (!hex.startsWith("#")) hex = "#$hex"
                try {
                    android.graphics.Color.parseColor(hex)
                    onColorSelected(hex.uppercase())
                } catch (e: Exception) {
                    android.widget.Toast.makeText(this, LocaleHelper.getString("invalid_hex").replace("%s", hex), android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun checkUpdate() {
        binding.btnCheckUpdate.isEnabled = false
        binding.tvUpdateStatus.visibility = View.VISIBLE
        binding.tvUpdateStatus.text = LocaleHelper.getString("checking")
        binding.btnDownloadUpdate.visibility = View.GONE
        binding.layoutDownloadProgress.visibility = View.GONE

        lifecycleScope.launch {
            val info = UpdateCheckerWithProgress.checkForUpdate(this@SettingsActivity)
            binding.btnCheckUpdate.isEnabled = true

            if (info != null) {
                binding.tvUpdateStatus.text = LocaleHelper.getString("new_version")
                    .replace("%s", info.versionName, ignoreCase = false)
                    .replaceFirst("%s", "\n${info.releaseNote}")
                currentDownloadUrl = info.downloadUrl
                binding.btnDownloadUpdate.visibility = View.VISIBLE
            } else {
                binding.tvUpdateStatus.text = LocaleHelper.getString("up_to_date")
            }
        }
    }

    private fun startDownload(url: String) {
        binding.btnDownloadUpdate.visibility = View.GONE
        binding.btnCheckUpdate.isEnabled = false
        binding.layoutDownloadProgress.visibility = View.VISIBLE
        binding.pbDownload.progress = 0
        binding.tvProgressPercent.text = "0%"

        UpdateCheckerWithProgress.downloadWithProgress(this, url, object : UpdateCheckerWithProgress.DownloadProgressListener {
            override fun onProgress(progress: Int) {
                runOnUiThread {
                    binding.pbDownload.progress = progress
                    binding.tvProgressPercent.text = LocaleHelper.getString("update_downloading").replace("%d%%", "$progress%")
                }
            }

            override fun onComplete() {
                runOnUiThread {
                    binding.tvProgressPercent.text = "100%"
                    binding.tvUpdateStatus.text = LocaleHelper.getString("update_done")
                    binding.btnCheckUpdate.isEnabled = true
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    binding.tvUpdateStatus.text = LocaleHelper.getString("update_error").replace("%s", error)
                    binding.btnCheckUpdate.isEnabled = true
                    binding.btnDownloadUpdate.visibility = View.VISIBLE
                    binding.layoutDownloadProgress.visibility = View.GONE
                }
            }
        })
    }
}
