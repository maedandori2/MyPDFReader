package com.mypdf.reader

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
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
        loadCurrentSettings()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvSettingsTitle.text = LocaleHelper.getString("settings_title")
        binding.tvDisplayTitle.text = LocaleHelper.getString("settings_title") // Reuse title for section
        binding.tvFontSizeLabel.text = LocaleHelper.getString("settings_file_name_size")
        binding.tvOpacityLabel.text = LocaleHelper.getString("settings_notice_opacity")
        binding.tvDurationLabel.text = LocaleHelper.getString("settings_notice_duration")
        binding.btnSaveSettings.text = LocaleHelper.getString("settings_save")

        binding.tvUpdateSectionTitle.text = LocaleHelper.getString("update_section")
        val currentVersion = packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvCurrentVersion.text = LocaleHelper.getString("current_version").replace("%s", currentVersion)
        binding.btnCheckUpdate.text = LocaleHelper.getString("check_update")
        binding.btnDownloadUpdate.text = LocaleHelper.getString("download")
    }

    private fun loadCurrentSettings() {
        val size = SettingsManager.getFileNameSize()
        val opacity = SettingsManager.getNoticeOpacity()
        val duration = SettingsManager.getNoticeDuration()

        binding.sbFontSize.apply {
            max = SettingsManager.MAX_FILE_NAME_SIZE - SettingsManager.MIN_FILE_NAME_SIZE
            progress = size - SettingsManager.MIN_FILE_NAME_SIZE
        }
        binding.tvFontSizeValue.text = size.toString()

        binding.sbOpacity.apply {
            max = SettingsManager.MAX_NOTICE_OPACITY - SettingsManager.MIN_NOTICE_OPACITY
            progress = opacity - SettingsManager.MIN_NOTICE_OPACITY
        }
        binding.tvOpacityValue.text = opacity.toString()

        binding.sbDuration.apply {
            max = SettingsManager.MAX_NOTICE_DURATION - SettingsManager.MIN_NOTICE_DURATION
            progress = duration - SettingsManager.MIN_NOTICE_DURATION
        }
        binding.tvDurationValue.text = duration.toString()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.sbFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvFontSizeValue.text = (progress + SettingsManager.MIN_FILE_NAME_SIZE).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.sbOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvOpacityValue.text = (progress + SettingsManager.MIN_NOTICE_OPACITY).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.sbDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvDurationValue.text = (progress + SettingsManager.MIN_NOTICE_DURATION).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSaveSettings.setOnClickListener {
            val size = binding.sbFontSize.progress + SettingsManager.MIN_FILE_NAME_SIZE
            val opacity = binding.sbOpacity.progress + SettingsManager.MIN_NOTICE_OPACITY
            val duration = binding.sbDuration.progress + SettingsManager.MIN_NOTICE_DURATION

            SettingsManager.setFileNameSize(size)
            SettingsManager.setNoticeOpacity(opacity)
            SettingsManager.setNoticeDuration(duration)

            Toast.makeText(this, LocaleHelper.getString("settings_save"), Toast.LENGTH_SHORT).show()
            finish()
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
                    binding.tvProgressPercent.text = LocaleHelper.getString("downloading").replace("%d%%", "$progress%")
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
