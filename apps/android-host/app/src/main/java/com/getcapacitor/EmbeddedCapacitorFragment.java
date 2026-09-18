package com.getcapacitor;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.jetsnack.R;
import com.example.jetsnack.profile.ProfileHostPlugin;
import ee.forgr.capacitor_updater.CapacitorUpdaterPlugin;

/**
 * Hosts one Capacitor bridge inside an existing application's Fragment hierarchy.
 *
 * <p>Capacitor's result-dispatch methods intentionally have package visibility.
 * Keeping this small adapter in {@code com.getcapacitor} lets the host activity
 * forward plugin callbacks without relying on reflection or a copied Capacitor
 * implementation.</p>
 */
public final class EmbeddedCapacitorFragment extends Fragment {

    public static final String TAG = "embedded-capacitor-profile";

    private Bridge bridge;
    private boolean skipWebHistoryOnNextBack;

    public EmbeddedCapacitorFragment() {
        super(R.layout.fragment_embedded_capacitor);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bridge.Builder builder = new Bridge.Builder(this).setInstanceState(savedInstanceState);
        // The embedded host does not run `npx cap sync`, so native plugins are
        // registered here rather than through Capacitor's generated registry.
        builder.addPlugin(CapacitorUpdaterPlugin.class);
        builder.addPlugin(ProfileHostPlugin.class);
        try {
            builder.addPlugins(new PluginManager(requireContext().getAssets()).loadPluginClasses());
        } catch (PluginLoadException error) {
            Logger.error("Unable to load Capacitor plugins.", error);
        }

        bridge = builder.create();
        // Local development remote runs over HTTP while Capacitor's bundled shell
        // is served from https://localhost. Production remotes must use HTTPS;
        // do not carry this test-only mixed-content override into a release app.
        bridge.getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        bridge.onNewIntent(requireActivity().getIntent());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (bridge != null) {
            bridge.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bridge != null) {
            bridge.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bridge != null) {
            bridge.getApp().fireStatusChange(true);
            bridge.onResume();
        }
    }

    @Override
    public void onPause() {
        if (bridge != null) {
            bridge.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (bridge != null) {
            bridge.getApp().fireStatusChange(false);
            bridge.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (bridge != null) {
            bridge.onDestroy();
            bridge = null;
        }
        super.onDestroyView();
    }

    public boolean forwardPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        return bridge != null && bridge.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean forwardActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        return bridge != null && bridge.onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(@NonNull Intent intent) {
        if (bridge != null) {
            bridge.onNewIntent(intent);
        }
    }

    public void forwardConfigurationChanged(@NonNull Configuration newConfig) {
        if (bridge != null) {
            bridge.onConfigurationChanged(newConfig);
        }
    }

    public boolean handleBack() {
        if (skipWebHistoryOnNextBack) {
            skipWebHistoryOnNextBack = false;
            return false;
        }
        if (bridge == null || !bridge.getWebView().canGoBack()) {
            return false;
        }
        bridge.getWebView().goBack();
        return true;
    }

    /** Forces the host destination to close without consuming a WebView history entry. */
    public void closeProfile() {
        skipWebHistoryOnNextBack = true;
    }
}
