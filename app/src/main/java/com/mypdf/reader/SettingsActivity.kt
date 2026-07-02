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
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.switchKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setKeepScreenOn(isChecked)
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
