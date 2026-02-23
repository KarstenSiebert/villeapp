package com.siehog.ville.ui.scanner;

import static android.content.Context.MODE_PRIVATE;
import static com.siehog.ville.httpclient.ClientFactory.JSON;
import static com.siehog.ville.httpclient.KeyHelper.getPublicKeyBase64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.snackbar.Snackbar;
import com.siehog.ville.R;
import com.siehog.ville.databinding.FragmentScannerBinding;
import com.siehog.ville.httpclient.ClientFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScannerFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String[]> permissionLauncher;
    private FragmentScannerBinding binding;
    private QRCodeScanner qrScanner;

    private int market;
    private double localLatitude;
    private double localLongitude;
    private double marketLatitude;
    private double marketLongitude;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        getCurrentLocation();
                    } else {
                        Snackbar.make(requireView(), "No location permission", Snackbar.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ScannerViewModel scannerViewModel =
                new ViewModelProvider(this).get(ScannerViewModel.class);

        binding = FragmentScannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        checkLocationPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qrScanner != null) {
            qrScanner.stopQRCodeScanning();
        }
    }

    private void startScanner() {
        if (qrScanner == null) qrScanner = new QRCodeScanner();
        else qrScanner.stopQRCodeScanning();

        qrScanner.startQRCodeScanner(requireContext(), requireActivity(),
                requireView().findViewById(R.id.previewView),
                qrText -> requireActivity().runOnUiThread(() -> decodeScannerData(qrText))
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

        } else {
            getCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                localLatitude = location.getLatitude();
                localLongitude = location.getLongitude();

                startScanner();

            } else {
                Snackbar.make(requireView(), "Location not available", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkRadius(double marketLat, double marketLng) {
        float radiusInMeters = 1000f;

        float[] results = new float[1];

        Location.distanceBetween(marketLat, marketLng, localLatitude, localLongitude, results);

        return results[0] <= radiusInMeters;
    }

    private void decodeScannerData(String base64String) {

        if (qrScanner != null) {
            qrScanner.stopQRCodeScanning();
        }

        try {
            JSONObject jsonObject = new JSONObject(base64String);

            market = jsonObject.getInt("market");
            marketLatitude = jsonObject.getDouble("latitude");
            marketLongitude = jsonObject.getDouble("longitude");

            if (checkRadius(marketLatitude, marketLongitude)) {

                try {
                    String publicKey = getPublicKeyBase64();

                    grabMarketData(publicKey, market);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                Snackbar.make(requireView(), "Outsite of radius", Snackbar.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Fehler beim Parsen des JSON!");
        }
    }

    private void grabMarketData(String publicKey, int market) {

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);

        String id = prefs.getString("device_id", null);

        OkHttpClient client = ClientFactory.getClient();

        if (client != null) {
            JSONObject payload = new JSONObject();

            try {
                payload.put("public_id", publicKey);
                payload.put("market_id", market);
                payload.put("device_id", id);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            RequestBody body = RequestBody.create(payload.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://www.tokenville.fun/api/clients")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                    if (response.code() == 200) {

                        try (ResponseBody responseBody = response.body()) {
                            JSONObject responseJson = new JSONObject(responseBody.string());

                            Snackbar.make(requireView(), responseJson.getString("hallo"), Snackbar.LENGTH_LONG).show();

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (response.code() == 401) {

                        try (ResponseBody responseBody = response.body()) {
                            JSONObject responseJson = new JSONObject(responseBody.string());

                            Snackbar.make(requireView(), responseJson.getString("message"), Snackbar.LENGTH_LONG).show();

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    response.body().close();
                }
            });
        }
    }

}