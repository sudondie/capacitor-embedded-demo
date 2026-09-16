package com.getcapacitor;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.jetsnack.R;

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

    public EmbeddedCapacitorFragment() {
        super(R.layout.fragment_embedded_capacitor);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bridge.Builder builder = new Bridge.Builder(this).setInstanceState(savedInstanceState);
        try {
            builder.addPlugins(new PluginManager(requireContext().getAssets()).loadPluginClasses());
        } catch (PluginLoadException error) {
            Logger.error("Unable to load Capacitor plugins.", error);
        }

        bridge = builder.create();
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
}
