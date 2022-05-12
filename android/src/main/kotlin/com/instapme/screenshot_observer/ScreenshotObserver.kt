package com.supernovel.screenshot_observer

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import java.io.File


// refer to https://github.com/nikit19/ScreenshotDetector
class ScreenshotObserver(private val contentResolver: ContentResolver, private val listener: Listener) {
    private var contentObserver: ContentObserver? = null

    fun observe() {
        if (contentObserver == null) {
            contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                    uri?.let { queryScreenshots(it) }
                }
            }

            contentResolver.registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver as ContentObserver
            )
        }
    }

    fun unobserve() {
        if (contentObserver != null) {
            contentResolver.unregisterContentObserver(contentObserver!!)
        }

        contentObserver = null
    }

    private fun queryScreenshots(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            queryRelativeDataColumn(uri)
        } else {
            queryDataColumn(uri)
        }
    }

    private fun queryDataColumn(uri: Uri) {
        val projection = arrayOf(
                MediaStore.Images.Media.DATA
        )
        
        contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
        )?.use { cursor ->
            val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                if (path.contains("screenshots", true)) {
                    listener.onScreenshot(path)
                }
            }
        }
    }

    private fun queryRelativeDataColumn(uri: Uri) {
        val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH
        )

        contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
        )?.use { cursor ->
            val relativePathColumn = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
            val displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val name = cursor.getString(displayNameColumn)
                val relativePath = cursor.getString(relativePathColumn)
                if (name.contains("screenshots", true) || relativePath.contains("screenshot", true)) {
                    listener.onScreenshot(File(relativePath, name).path)
                }
            }
        }
    }

    interface Listener {
        fun onScreenshot(path: String?)
    }
}