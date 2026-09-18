/*
 * Copyright 2020-2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.example.jetsnack.widget.RecentOrdersWidgetReceiver
import com.getcapacitor.EmbeddedCapacitorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var embeddedProfileCloser: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lifecycleScope.launch(Dispatchers.Default) {
                setWidgetPreviews()
            }
        }
        setContent { JetsnackApp() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        val handled = embeddedCapacitorFragment()?.forwardPermissionResult(
            requestCode,
            permissions,
            grantResults,
        ) == true
        if (!handled) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @Deprecated("Deprecated in AndroidX; retained for Capacitor plugin compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val handled = embeddedCapacitorFragment()?.forwardActivityResult(requestCode, resultCode, data) == true
        if (!handled) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        embeddedCapacitorFragment()?.onNewIntent(intent)
    }

    /** Skip WebView history and dismiss the full-screen native destination. */
    fun closeEmbeddedProfile() {
        embeddedProfileCloser?.invoke()
    }

    fun setEmbeddedProfileCloser(closer: (() -> Unit)?) {
        embeddedProfileCloser = closer
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        embeddedCapacitorFragment()?.forwardConfigurationChanged(newConfig)
    }

    private fun embeddedCapacitorFragment(): EmbeddedCapacitorFragment? =
        supportFragmentManager.findFragmentByTag(EmbeddedCapacitorFragment.TAG) as? EmbeddedCapacitorFragment

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun setWidgetPreviews() {
        val receiver = RecentOrdersWidgetReceiver::class
        val installedProviders = getSystemService(AppWidgetManager::class.java).installedProviders
        val providerInfo = installedProviders.firstOrNull {
            it.provider.className ==
                receiver.qualifiedName
        }
        providerInfo?.generatedPreviewCategories.takeIf { it == 0 }?.let {
            // Set previews if this provider if unset
            GlanceAppWidgetManager(this).setWidgetPreviews(receiver)
        }
    }
}
