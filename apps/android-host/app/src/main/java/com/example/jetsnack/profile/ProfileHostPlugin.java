package com.example.jetsnack.profile;

import com.example.jetsnack.ui.MainActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

/** Native boundary exposed only through the ProfilePlatform adapter in the web shell. */
@CapacitorPlugin(
    name = "ProfileHost",
    permissions = {
        @Permission(
            alias = "location",
            strings = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            }
        ),
    }
)
public final class ProfileHostPlugin extends Plugin {
    private static final String LOCATION_PERMISSION = "location";

    @PluginMethod
    public void getShopperContext(PluginCall call) {
        JSObject context = new JSObject();
        context.put("name", "Анна Крылова");
        context.put("email", "anna@jetsnack.app");
        context.put("favoriteSnacks", 12);
        context.put("loyaltyPoints", 480);
        call.resolve(context);
    }

    @PluginMethod
    public void feedbackSuccess(PluginCall call) {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(35);
            }
        }
        call.resolve();
    }

    /**
     * The native shell owns both the Android permission and location API. Web
     * bundles (including remotes) reach it only through ProfilePlatform.
     */
    @PluginMethod
    public void getCurrentLocation(PluginCall call) {
        if (getPermissionState(LOCATION_PERMISSION) != com.getcapacitor.PermissionState.GRANTED) {
            requestPermissionForAlias(LOCATION_PERMISSION, call, "locationPermissionCallback");
            return;
        }
        resolveCurrentLocation(call);
    }

    @PermissionCallback
    private void locationPermissionCallback(PluginCall call) {
        if (getPermissionState(LOCATION_PERMISSION) != com.getcapacitor.PermissionState.GRANTED) {
            call.reject("Разрешение на геолокацию не выдано.");
            return;
        }
        resolveCurrentLocation(call);
    }

    @SuppressWarnings("deprecation")
    private void resolveCurrentLocation(PluginCall call) {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ? LocationManager.GPS_PROVIDER
            : LocationManager.NETWORK_PROVIDER;
        Location lastLocation = locationManager.getLastKnownLocation(provider);
        if (lastLocation != null) {
            resolveLocation(call, lastLocation);
            return;
        }
        locationManager.requestSingleUpdate(provider, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                resolveLocation(call, location);
            }
        }, Looper.getMainLooper());
    }

    private void resolveLocation(PluginCall call, Location location) {
        JSObject result = new JSObject();
        result.put("latitude", location.getLatitude());
        result.put("longitude", location.getLongitude());
        call.resolve(result);
    }

    @PluginMethod
    public void track(PluginCall call) {
        Log.i("ProfileHost", "Analytics event: " + call.getString("name", "unknown"));
        call.resolve();
    }

    @PluginMethod
    public void closeProfile(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).closeEmbeddedProfile();
            } else {
                getActivity().onBackPressed();
            }
        });
        call.resolve();
    }
}
