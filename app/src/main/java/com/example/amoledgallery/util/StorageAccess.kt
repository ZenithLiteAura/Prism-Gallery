package com.example.amoledgallery.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * 「所有文件访问」权限：浏览目录、直接读隐藏/未被媒体库收录的文件时需要。
 *
 * - Android 11+（API 30）：`MANAGE_EXTERNAL_STORAGE`，由系统设置里的开关授予，
 *   应用只能跳转到 `ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION` 让用户自己打开。
 * - Android 10（API 29）：没有这个开关，用 `WRITE_EXTERNAL_STORAGE`（配合清单里的
 *   `requestLegacyExternalStorage`）就能读整个外部存储。
 */
object StorageAccess {

    fun isGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching { Environment.isExternalStorageManager() }.getOrDefault(false)
        } else {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /** "所有文件访问" 的系统设置页；API 29 没有这一页（返回 null，改申请运行时写权限）。 */
    fun settingsIntent(context: Context): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return null
        val perApp = Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        val resolvable = perApp.resolveActivity(context.packageManager) != null
        return if (resolvable) perApp else Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
    }

    /** API 29 上要申请的运行时权限（30+ 返回 null）。 */
    fun legacyRuntimePermission(): String? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else {
            null
        }
}