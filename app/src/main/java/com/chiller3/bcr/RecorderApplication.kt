/*
 * SPDX-FileCopyrightText: 2022-2026 Andrew Gunnerson
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.chiller3.bcr

import android.app.Application
import android.util.Log
import androidx.core.net.toFile
import com.chiller3.bcr.output.OutputDirUtils
import com.google.android.material.color.DynamicColors

class RecorderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Logcat.init(this)

        val oldCrashHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            try {
                val redactor = OutputDirUtils.NULL_REDACTOR
                val dirUtils = OutputDirUtils(this, redactor)
                val logcatPath = listOf(Logcat.FILENAME_CRASH)
                val logcatFile = dirUtils.createFileInDefaultDirBestEffort(logcatPath, Logcat.MIMETYPE)

                if (logcatFile == null) {
                    Log.w(TAG, "Failed to create crash log file for uncaught exception in $t")
                } else {
                    Log.e(TAG, "Saving logcat to ${redactor.redact(logcatFile.uri)} due to uncaught exception in $t", e)

                    try {
                        Logcat.dump(logcatFile.uri.toFile())
                    } catch (dumpException: Exception) {
                        Log.w(TAG, "Failed to dump crash log for uncaught exception in $t", dumpException)
                    } finally {
                        try {
                            dirUtils.moveToOutputDir(logcatFile, logcatPath, Logcat.MIMETYPE)
                        } catch (_: Exception) {
                            // Ignore.
                        }
                    }
                }
            } catch (handlerException: Exception) {
                Log.w(TAG, "Failed while handling uncaught exception in $t", handlerException)
            } finally {
                oldCrashHandler?.uncaughtException(t, e)
            }
        }

        // Enable Material You colors
        DynamicColors.applyToActivitiesIfAvailable(this)

        Notifications(this).updateChannels()

        Preferences(this).migrateTemplate()
    }

    companion object {
        private val TAG = RecorderApplication::class.java.simpleName
    }
}
