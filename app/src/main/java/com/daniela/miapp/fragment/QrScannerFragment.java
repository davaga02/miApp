package com.daniela.miapp.fragment;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.daniela.miapp.R;
import com.daniela.miapp.fragment.CrearPedidoFragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class QrScannerFragment extends Fragment {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private boolean isScanned = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_scanner, container, false);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        previewView = view.findViewById(R.id.cameraPreview);
        cameraExecutor = Executors.newSingleThreadExecutor();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));

        // Botón para volver
        Button btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void bindCamera(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

        ImageAnalysis analysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysis.setAnalyzer(cameraExecutor, imageProxy -> {
            @SuppressLint("UnsafeOptInUsageError")
            ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
            if (planes.length > 0 && !isScanned) {
                @SuppressLint("UnsafeOptInUsageError")
                Image mediaImage = imageProxy.getImage();
                if (mediaImage != null) {
                    InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                    BarcodeScanner scanner = BarcodeScanning.getClient();
                    scanner.process(image)
                            .addOnSuccessListener(barcodes -> {
                                if (!barcodes.isEmpty()) {
                                    Barcode barcode = barcodes.get(0);
                                    String value = barcode.getRawValue();
                                    isScanned = true;

                                    Toast.makeText(requireContext(), "Escaneado: " + value, Toast.LENGTH_SHORT).show();

                                    // Ir a CrearPedidoFragment con la mesa cargada
                                    CrearPedidoFragment fragment = new CrearPedidoFragment();
                                    Bundle args = new Bundle();
                                    args.putString("mesaCliente", value);
                                    fragment.setArguments(args);
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frameContainerCliente, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            })
                            .addOnFailureListener(e -> Log.e("QR_SCAN", "Error escaneando", e))
                            .addOnCompleteListener(task -> imageProxy.close());
                }
            } else {
                imageProxy.close();
            }
        });

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner) this, selector, preview, analysis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}

