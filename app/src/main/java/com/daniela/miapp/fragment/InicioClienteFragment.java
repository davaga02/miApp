package com.daniela.miapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import android.app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


public class InicioClienteFragment extends Fragment {

    private Button btnEscanearQR;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false);

        btnEscanearQR = view.findViewById(R.id.btnEscanearQR);

        btnEscanearQR.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, new QrScannerFragment()) // Usa tu fragmento
                    .addToBackStack(null) // Esto permite volver atrás
                    .commit();
        });

        return view;
    }

    // Nuevo launcher moderno para escanear QR
    private final ActivityResultLauncher<Intent> qrLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                    if (scanResult != null && scanResult.getContents() != null) {
                        String mesaEscaneada = scanResult.getContents().trim(); // Ej: "Mesa 1" o "TAKEAWAY"

                        // Redirigir al fragmento de crear pedido
                        CrearPedidoFragment fragment = CrearPedidoFragment.newInstanceParaCliente(mesaEscaneada);
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameContainer, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "QR cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            });
/*
    private void lanzarEscanerQR() {
        IntentIntegrator integrator = new IntentIntegrator(requireActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanea el QR de tu mesa o para llevar");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);

        Intent intent = integrator.createScanIntent();
        qrLauncher.launch(intent);
    }

 */
}
