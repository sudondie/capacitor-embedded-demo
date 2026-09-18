package com.example.jetsnack.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import com.example.jetsnack.R
import com.getcapacitor.EmbeddedCapacitorFragment

/** Places the fragment-based Capacitor bridge in a Compose destination. */
@Composable
fun EmbeddedCapacitorScreen(modifier: Modifier = Modifier) {
    val activity = androidx.compose.ui.platform.LocalContext.current as AppCompatActivity
    val fragmentManager = activity.supportFragmentManager
    var interceptBack by remember { mutableStateOf(true) }

    // This is inside the root Compose destination, so it wins over NavHost's
    // default Back handling. Native navigation is used only after WebView history.
    BackHandler(enabled = interceptBack) {
        val handledByWebView = (fragmentManager.findFragmentByTag(EmbeddedCapacitorFragment.TAG) as? EmbeddedCapacitorFragment)
            ?.handleBack() == true
        if (!handledByWebView) {
            // Do not re-enter OnBackPressedDispatcher from this handler: on
            // predictive Back that recursively invokes the same callback.
            (activity as? MainActivity)?.closeEmbeddedProfile() ?: activity.finish()
        }
    }

    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = R.id.embedded_capacitor_container
            }
        },
        modifier = modifier.fillMaxSize(),
    )

    DisposableEffect(fragmentManager) {
        if (fragmentManager.findFragmentByTag(EmbeddedCapacitorFragment.TAG) == null && !fragmentManager.isStateSaved) {
            fragmentManager
                .beginTransaction()
                .add(
                    R.id.embedded_capacitor_container,
                    EmbeddedCapacitorFragment(),
                    EmbeddedCapacitorFragment.TAG,
                ).commitNow()
        }

        onDispose {
            if (!fragmentManager.isStateSaved) {
                fragmentManager.findFragmentByTag(EmbeddedCapacitorFragment.TAG)?.let { fragment ->
                    fragmentManager.beginTransaction().remove(fragment).commitNow()
                }
            }
        }
    }
}
