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
import com.example.BuildConfig
import com.example.ui.theme.GreenPrimary
import com.example.updater.UpdateState

@Composable
fun UpdateDialog(
    updateState: UpdateState,
    onDismiss: () -> Unit,
    onDownload: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تحديث التطبيق",
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("جاري تنزيل ملف التحديث... ${updateState.progressPercent}%")
                        LinearProgressIndicator(
                            progress = { updateState.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = GreenPrimary
                        )
                    }
                }
                is UpdateState.ReadyToInstall -> {
                    Text("تم تنزيل التحديث بنجاح. سيتم فتح برنامج التثبيت الآن.")
                }
                is UpdateState.Error -> {
                    Text("تعذر إتمام الفحص: ${updateState.message}")
                }
                UpdateState.Idle -> {}
            }
        },
        confirmButton = {
            if (updateState is UpdateState.UpdateAvailable) {
                Button(
                    onClick = { onDownload(updateState.downloadUrl) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("تنزيل وتثبيت")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("حسناً")
                }
            }
        },
        dismissButton = {
            if (updateState is UpdateState.UpdateAvailable) {
                TextButton(onClick = onDismiss) {
                    Text("لاحقاً")
                }
            }
        }
    )
}
