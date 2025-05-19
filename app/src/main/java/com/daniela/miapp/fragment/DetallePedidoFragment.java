package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class DetallePedidoFragment extends Fragment {

    private TextView tvMesa, tvEstado, tvProductos;
    private Button btnEditar, btnPreparacion, btnCompletado;

    private FirebaseFirestore db;
    private String pedidoId;

    public static DetallePedidoFragment newInstance(Pedido pedido) {
        DetallePedidoFragment fragment = new DetallePedidoFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedido.getId());
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
        tvProductos = view.findViewById(R.id.tvProductos);

        btnEditar = view.findViewById(R.id.btnEditarPedido);
        btnPreparacion = view.findViewById(R.id.btnEstadoPreparacion);
        btnCompletado = view.findViewById(R.id.btnEstadoCompletado);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            pedidoId = getArguments().getString("pedidoId");
            cargarDatosPedido();
        }

        btnPreparacion.setOnClickListener(v -> actualizarEstado("En preparación"));
        btnCompletado.setOnClickListener(v -> actualizarEstado("Completado"));
        btnEditar.setOnClickListener(v -> {
            // Puedes reutilizar CrearPedidoFragment con modo edición
            CrearPedidoFragment fragment = CrearPedidoFragment.newInstance(pedidoId);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        ImageButton btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void cargarDatosPedido() {
        db.collection("pedidos").document(pedidoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String mesa = doc.getString("mesa");
                        String estado = doc.getString("estado");
                        List<Map<String, Object>> productos = (List<Map<String, Object>>) doc.get("productos");

                        tvMesa.setText("Mesa: " + mesa);
                        tvEstado.setText("Estado: " + estado);

                        StringBuilder detalle = new StringBuilder();
                        for (Map<String, Object> prod : productos) {
                            String nombre = (String) prod.get("nombre");
                            Long cantidad = (Long) prod.get("cantidad");
                            detalle.append("- ").append(nombre).append(" x").append(cantidad).append("\n");
                        }
                        tvProductos.setText(detalle.toString());
                    }
                });
    }

    private void actualizarEstado(String nuevoEstado) {
        db.collection("pedidos").document(pedidoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
    }
}
