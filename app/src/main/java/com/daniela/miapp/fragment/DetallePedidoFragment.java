package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetallePedidoFragment extends Fragment {

    private TextView tvMesa, tvEstado, tvFecha, tvCreador, tvProductos, tvTotal;
    private Button btnVolver;

    private String pedidoId;

    public static DetallePedidoFragment newInstance(String pedidoId) {
        DetallePedidoFragment fragment = new DetallePedidoFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedidoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvMesa = view.findViewById(R.id.tvMesa);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvFecha = view.findViewById(R.id.tvFecha);
        tvCreador = view.findViewById(R.id.tvCreador);
        tvProductos = view.findViewById(R.id.tvProductos);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnVolver = view.findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        if (getArguments() != null) {
            pedidoId = getArguments().getString("pedidoId");
            cargarDatos();
        }
    }

    private void cargarDatos() {
        FirebaseFirestore.getInstance().collection("pedidos").document(pedidoId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvMesa.setText("Mesa: " + doc.getString("mesa"));
                        tvEstado.setText("Estado: " + doc.getString("estado"));

                        long timestamp = doc.getLong("timestamp");
                        String fecha = new java.text.SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
                                .format(new java.util.Date(timestamp));
                        tvFecha.setText("Fecha: " + fecha);

                        tvCreador.setText("Realizado por: " + doc.getString("correoUsuario"));

                        List<Map<String, Object>> productos = (List<Map<String, Object>>) doc.get("productos");
                        double total = 0.0;
                        StringBuilder productosTexto = new StringBuilder();

                        for (Map<String, Object> prod : productos) {
                            String nombre = (String) prod.get("nombre");
                            long cantidad = prod.get("cantidad") != null ? (long) prod.get("cantidad") : 0;
                            String tamaño = prod.containsKey("tamaño") ? (String) prod.get("tamaño") : null;
                            String sabor = prod.containsKey("sabor") ? (String) prod.get("sabor") : null;

                            Double subtotalObj = prod.get("subtotal") instanceof Double ? (Double) prod.get("subtotal") : null;
                            double subtotal = subtotalObj != null ? subtotalObj : 0.0;

                            total += subtotal;

                            productosTexto.append("• ").append(nombre != null ? nombre : "Producto")
                                    .append(" x").append(cantidad);
                            if (tamaño != null) productosTexto.append(" (").append(tamaño).append(")");
                            if (sabor != null) productosTexto.append(" - ").append(sabor);
                            productosTexto.append("\n");
                        }

                        tvProductos.setText(productosTexto.toString().trim());
                        tvTotal.setText(String.format("Total: %.2f€", total));
                    }
                });
    }
}
