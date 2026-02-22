package com.siehog.ville.ui.scanner;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.siehog.ville.R;
import com.siehog.ville.databinding.FragmentScannerBinding;

public class ScannerFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String[]> permissionLauncher;
    private final static int GEOLOCATION_ACCESS = 20;
    private FragmentScannerBinding binding;
    private QRCodeScanner qrScanner;

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
                        Toast.makeText(getContext(),
                                "Location Permission verweigert",
                                Toast.LENGTH_SHORT).show();
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
                qrText -> requireActivity().runOnUiThread(() -> handleQrText(qrText))
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleQrText(String qrText) {

        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireActivity(), qrText, Toast.LENGTH_SHORT).show());
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
                boolean inside = checkRadius(location.getLatitude(), location.getLongitude());

                Toast.makeText(getContext(),
                        "Im Radius: " + inside,
                        Toast.LENGTH_SHORT).show();

                startScanner();
            } else {
                Toast.makeText(getContext(),
                        "Standort nicht verfügbar",
                        Toast.LENGTH_SHORT).show();

                startScanner();
            }
        });
    }

    private boolean checkRadius(double userLat, double userLng) {
        double targetLat = 52.5200;
        double targetLng = 13.4050;
        float radiusInMeters = 1000f;

        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, targetLat, targetLng, results);
        return results[0] <= radiusInMeters;
    }

}