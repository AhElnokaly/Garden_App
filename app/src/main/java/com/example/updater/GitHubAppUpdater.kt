package com.example.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

sealed interface UpdateState {
    object Idle : UpdateState
    object Checking : UpdateState
    data class UpdateAvailable(
        val latestVersion: String,
        val downloadUrl: String,
        val releaseNotes: String?
    ) : UpdateState
    object UpToDate : UpdateState
    data class Downloading(val progressPercent: Int) : UpdateState
    data class ReadyToInstall(val apkFile: File) : UpdateState
    data class Error(val message: String) : UpdateState
}

class GitHubAppUpdater(
    private val context: Context,
    private val repoOwner: String = "AhElnokaly",
    private val repoName: String = "Garden_App"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    suspend fun checkForUpdates(): UpdateState = withContext(Dispatchers.IO) {
        _updateState.value = UpdateState.Checking
        try {
            val url = "https://api.github.com/repos/$repoOwner/$repoName/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GardenCompanion-App")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorMsg = when (response.code) {
                    404 -> "لا يوجد إصدار جديد منشور حالياً على المستودع ($repoOwner/$repoName)"
                    403 -> "تم تجاوز الحد المؤقت لطلبات GitHub، يرجى المحاولة لاحقاً"
                    else -> "فشل فحص التحديثات: رمز ${response.code}"
                }
                val state = UpdateState.Error(errorMsg)
                _updateState.value = state
                return@withContext state
            }

            val body = response.body?.string().orEmpty()
            val json = JSONObject(body)
            val tagName = json.optString("tag_name", "").removePrefix("v").trim()
            val bodyText = json.optString("body", "")

            val assets = json.optJSONArray("assets")
            var downloadUrl: String? = null
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        downloadUrl = asset.optString("browser_download_url")
                        break
                    }
                }
            }

            val currentVersion = BuildConfig.VERSION_NAME.removePrefix("v").trim()
            if (isNewerVersion(tagName, currentVersion) && !downloadUrl.isNullOrBlank()) {
                val state = UpdateState.UpdateAvailable(
                    latestVersion = tagName,
                    downloadUrl = downloadUrl,
                    releaseNotes = bodyText
                )
                _updateState.value = state
                state
            } else {
                val state = UpdateState.UpToDate
                _updateState.value = state
                state
            }
        } catch (e: Exception) {
            val state = UpdateState.Error("خطأ أثناء فحص التحديث: ${e.localizedMessage ?: "غير معروف"}")
            _updateState.value = state
            state
        }
    }

    suspend fun downloadAndInstallApk(downloadUrl: String) = withContext(Dispatchers.IO) {
        try {
            _updateState.value = UpdateState.Downloading(progressPercent = 0)
            val request = Request.Builder().url(downloadUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                _updateState.value = UpdateState.Error("فشل تحميل ملف التحديث")
                return@withContext
            }

            val responseBody = response.body ?: run {
                _updateState.value = UpdateState.Error("محتوى التحديث فارغ")
                return@withContext
            }

            val contentLength = responseBody.contentLength()
            val cacheDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val apkFile = File(cacheDir, "GardenCompanion_update.apk")

            responseBody.byteStream().use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var totalRead: Long = 0
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        currentCoroutineContext().ensureActive()
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        if (contentLength > 0) {
                            val progress = ((totalRead * 100) / contentLength).toInt()
                            _updateState.value = UpdateState.Downloading(progressPercent = progress)
                        }
                    }
                    output.flush()
                }
            }

            _updateState.value = UpdateState.ReadyToInstall(apkFile)
            installApk(apkFile)
        } catch (e: CancellationException) {
            cleanupTempFiles()
            _updateState.value = UpdateState.Idle
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error("خطأ في تنزيل التحديث: ${e.localizedMessage}")
        }
    }

    fun cancelDownload() {
        cleanupTempFiles()
        _updateState.value = UpdateState.Idle
    }

    private fun cleanupTempFiles() {
        try {
            val cacheDir = File(context.cacheDir, "updates")
            val apkFile = File(cacheDir, "GardenCompanion_update.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }
        } catch (_: Exception) {}
    }

    fun installApk(apkFile: File) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error("تعذر فتح ملف التثبيت: ${e.localizedMessage}")
        }
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }

    private fun isNewerVersion(remote: String, current: String): Boolean {
        if (remote.isBlank() || current.isBlank()) return false
        val rParts = remote.split(".").mapNotNull { it.toIntOrNull() }
        val cParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(rParts.size, cParts.size)
        for (i in 0 until maxLen) {
            val r = rParts.getOrElse(i) { 0 }
            val c = cParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
