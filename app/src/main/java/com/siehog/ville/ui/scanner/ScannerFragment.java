package com.siehog.ville.ui.scanner;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.siehog.ville.R;
import com.siehog.ville.databinding.FragmentScannerBinding;

public class ScannerFragment extends Fragment {

    private FragmentScannerBinding binding;
    private QRCodeScanner qrScanner;

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
    public void onResume() {
        super.onResume();
        startScanner();
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

}