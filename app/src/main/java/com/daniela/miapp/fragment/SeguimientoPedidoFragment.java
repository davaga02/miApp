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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class SeguimientoPedidoFragment extends Fragment {

    private String pedidoId;
    private TextView tvEstado;
    private ListenerRegistration listener;

    public static SeguimientoPedidoFragment newInstance(String pedidoId) {
        SeguimientoPedidoFragment fragment = new SeguimientoPedidoFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedidoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seguimiento_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvEstado = view.findViewById(R.id.tvEstadoPedido);
        pedidoId = getArguments().getString("pedidoId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listener = db.collection("pedidos").document(pedidoId)
                .addSnapshotListener((doc, error) -> {
                    if (doc != null && doc.exists()) {
                        String estado = doc.getString("estado");
                        tvEstado.setText("Estado actual: " + estado);

                        if ("Completado".equalsIgnoreCase(estado)) {
                            Toast.makeText(getContext(), "🎉 ¡Tu pedido está listo!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Button btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listener != null) listener.remove();
    }
}
