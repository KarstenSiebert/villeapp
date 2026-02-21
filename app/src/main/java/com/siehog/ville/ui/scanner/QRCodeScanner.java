package com.siehog.ville.ui.scanner;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRCodeScanner {

    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private QRAnalyzer qrAnalyzer;

    public interface QRCodeFoundListener {
        void onQRCodeFound(String qrText);
    }

    public void startQRCodeScanner(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView, QRCodeFoundListener listener) {
        qrAnalyzer = new QRAnalyzer(context, listener);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, qrAnalyzer);

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void stopQRCodeScanning() {
        if (qrAnalyzer != null) {
            qrAnalyzer.stopScanning();
        }
    }

    private static class QRAnalyzer implements ImageAnalysis.Analyzer {
        private final BarcodeScanner scanner = BarcodeScanning.getClient();
        private final Context context;
        private final QRCodeFoundListener listener;
        private boolean isScanningStopped = false;

        public QRAnalyzer(Context context, QRCodeFoundListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @ExperimentalGetImage
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            if (isScanningStopped || imageProxy.getImage() == null) {
                imageProxy.close();
                return;
            }

            InputImage image = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (rawValue != null) {
                                isScanningStopped = true;

                                vibrate();

                                if (listener != null) {
                                    listener.onQRCodeFound(rawValue);
                                }

                                break;
                            }
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("QRCodeScanner", "Scan-Error", e);
                        imageProxy.close();
                    });
        }

        private void vibrate() {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }

        public void stopScanning() {
            isScanningStopped = true;
        }

        public void restartScanning() {
            isScanningStopped = false;
        }
    }

    public void restartQRCodeScanning() {
        if (qrAnalyzer != null) {
            qrAnalyzer.restartScanning();
        }
    }

}
