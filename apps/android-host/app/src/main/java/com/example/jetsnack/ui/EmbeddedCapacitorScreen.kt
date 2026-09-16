package com.example.jetsnack.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import com.example.jetsnack.R
import com.getcapacitor.EmbeddedCapacitorFragment

/** Places the fragment-based Capacitor bridge in a Compose destination. */
@Composable
fun EmbeddedCapacitorScreen(modifier: Modifier = Modifier) {
    val activity = androidx.compose.ui.platform.LocalContext.current as AppCompatActivity
    val fragmentManager = activity.supportFragmentManager

    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = R.id.embedded_capacitor_container
            }
        },
        modifier = modifier,
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
