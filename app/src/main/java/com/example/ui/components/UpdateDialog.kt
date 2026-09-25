package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.ui.theme.GreenPrimary
import com.example.updater.UpdateState
import java.io.File

@Composable
fun UpdateDialog(
    updateState: UpdateState,
    onDismiss: () -> Unit,
    onDownload: (String) -> Unit,
    onCancelDownload: () -> Unit = onDismiss,
    onInstall: (File) -> Unit = {}
) {
    val isBusy = updateState is UpdateState.Downloading || updateState is UpdateState.ReadyToInstall

    AlertDialog(
        onDismissRequest = {
            if (!isBusy) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isBusy,
            dismissOnClickOutside = !isBusy
        ),
        title = {
            Text(
                text = when (updateState) {
                    is UpdateState.Downloading -> "جاري تنزيل التحديث"
                    is UpdateState.ReadyToInstall -> "اكتمل التنزيل بنجاح"
                    else -> "تحديث التطبيق"
                },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            when (updateState) {
                is UpdateState.Checking -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("جاري فحص الإصدارات الجديدة عبر GitHub...")
                    }
                }
                is UpdateState.UpdateAvailable -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "يتوفر إصدار جديد: v${updateState.latestVersion}",
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Text(text = "الإصدار الحالي: v${BuildConfig.VERSION_NAME}")
                        if (!updateState.releaseNotes.isNullOrBlank()) {
                            Text(
                                text = "ملاحظات التحديث:\n${updateState.releaseNotes}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                is UpdateState.UpToDate -> {
                    Text("أنت تستخدم أحدث إصدار من تطبيق رفيق الحديقة (v${BuildConfig.VERSION_NAME}).")
                }
                is UpdateState.Downloading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("جاري تنزيل ملف التحديث... ${updateState.progressPercent}%")
                        LinearProgressIndicator(
                            progress = { updateState.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = GreenPrimary
                        )
                        Text(
                            text = "يرجى الانتظار حتى اكتمال التنزيل، ولا تغلق التطبيق.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is UpdateState.ReadyToInstall -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("تم تنزيل ملف التحديث بنجاح.")
                        Text(
                            text = "اضغط على \"تثبيت التحديث الآن\" للبدء.",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                is UpdateState.Error -> {
                    Text("تعذر إتمام العملية: ${updateState.message}")
                }
                UpdateState.Idle -> {}
            }
        },
        confirmButton = {
            when (updateState) {
                is UpdateState.UpdateAvailable -> {
                    Button(
                        onClick = { onDownload(updateState.downloadUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("تنزيل وتثبيت")
                    }
                }
                is UpdateState.ReadyToInstall -> {
                    Button(
                        onClick = { onInstall(updateState.apkFile) },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("تثبيت التحديث الآن")
                    }
                }
                is UpdateState.Downloading -> {
                    // Hidden during download to prevent accidental confirmation
                }
                else -> {
                    TextButton(onClick = onDismiss) {
                        Text("حسناً")
                    }
                }
            }
        },
        dismissButton = {
            when (updateState) {
                is UpdateState.UpdateAvailable -> {
                    TextButton(onClick = onDismiss) {
                        Text("لاحقاً")
                    }
                }
                is UpdateState.Downloading -> {
                    TextButton(onClick = onCancelDownload) {
                        Text("إلغاء التنزيل")
                    }
                }
                is UpdateState.ReadyToInstall -> {
                    TextButton(onClick = onDismiss) {
                        Text("إغلاق")
                    }
                }
                else -> {}
            }
        }
    )
}
