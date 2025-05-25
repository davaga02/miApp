package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        TextView tvTotal = view.findViewById(R.id.tvTotalPagar);
        Button btnPagar = view.findViewById(R.id.btnPagar);

        double total = getArguments().getDouble("total", 0.0);
        tvTotal.setText(String.format("Total a pagar: %.2f€", total));

        btnPagar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "✅ Pago simulado correctamente", Toast.LENGTH_SHORT).show();

            String pedidoId = getArguments().getString("pedidoId");

            SeguimientoPedidoFragment fragment = SeguimientoPedidoFragment.newInstance(pedidoId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainerCliente, fragment) // usa el mismo container
                    .addToBackStack(null)
                    .commit();
        });
    }
}
