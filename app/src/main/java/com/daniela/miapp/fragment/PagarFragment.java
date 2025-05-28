package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;

public class PagarFragment extends Fragment {

    public static PagarFragment newInstance(String pedidoId, double total) {
        PagarFragment fragment = new PagarFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedidoId);
        args.putDouble("total", total);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText etNumeroTarjeta = view.findViewById(R.id.etNumeroTarjeta);
        EditText etFecha = view.findViewById(R.id.etFecha);
        EditText etCvv = view.findViewById(R.id.etCvv);
        Button btnPagar = view.findViewById(R.id.btnPagar);

        btnPagar.setOnClickListener(v -> {
            String numTarjeta = etNumeroTarjeta.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();

            if (numTarjeta.length() != 16) {
                etNumeroTarjeta.setError("Debe tener 16 dígitos");
                return;
            }

            if (!fecha.matches("\\d{2}/\\d{2}")) {
                etFecha.setError("Formato inválido (MM/AA)");
                return;
            }

            if (cvv.length() != 3) {
                etCvv.setError("Debe tener 3 dígitos");
                return;
            }

            Toast.makeText(getContext(), "✅ Pago simulado correctamente", Toast.LENGTH_SHORT).show();

            String pedidoId = getArguments().getString("pedidoId");
            SeguimientoPedidoFragment fragment = SeguimientoPedidoFragment.newInstance(pedidoId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

    }
}
