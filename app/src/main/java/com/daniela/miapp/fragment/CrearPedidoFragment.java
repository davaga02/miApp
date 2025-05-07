package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.Producto;
import com.daniela.miapp.R;
import com.daniela.miapp.adapter.ProductoPedidoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class CrearPedidoFragment extends Fragment {

    private Spinner spinnerMesas;
    private RecyclerView recyclerProductos;
    private Button btnConfirmarPedido;

    private ProductoPedidoAdapter adapter;
    private FirebaseFirestore db;

    private List<Producto> listaProductos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spinnerMesas = view.findViewById(R.id.spinnerMesas);
        recyclerProductos = view.findViewById(R.id.recyclerProductosPedido);
        btnConfirmarPedido = view.findViewById(R.id.btnConfirmarPedido);
        db = FirebaseFirestore.getInstance();

        // Llenar el spinner de mesas (ejemplo: Mesa 1 a Mesa 10)
        List<String> mesas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mesas.add("Mesa " + i);
        }
        ArrayAdapter<String> adapterMesa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, mesas);
        adapterMesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMesas.setAdapter(adapterMesa);

        // Configurar RecyclerView
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoPedidoAdapter(listaProductos);
        recyclerProductos.setAdapter(adapter);

        cargarProductosDesdeFirestore();

        btnConfirmarPedido.setOnClickListener(v -> {
            String mesaSeleccionada = (String) spinnerMesas.getSelectedItem();
            String usuarioId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : "empleado";

            Map<String, Integer> productosSeleccionados = adapter.getCantidadesSeleccionadas();

            // Filtrar productos con cantidad > 0
            Map<String, Integer> productosFinal = new HashMap<>();
            for (Map.Entry<String, Integer> entry : productosSeleccionados.entrySet()) {
                if (entry.getValue() > 0) {
                    productosFinal.put(entry.getKey(), entry.getValue());
                }
            }

            if (productosFinal.isEmpty()) {
                Toast.makeText(requireContext(), "Agrega al menos un producto", Toast.LENGTH_SHORT).show();
                return;
            }

            Pedido pedido = new Pedido(
                    UUID.randomUUID().toString(),
                    usuarioId,
                    mesaSeleccionada,
                    productosFinal,
                    "Pendiente",
                    System.currentTimeMillis()
            );

            db.collection("pedidos")
                    .document(pedido.getId())
                    .set(pedido)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(requireContext(), "Pedido creado", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error al crear pedido", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void cargarProductosDesdeFirestore() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(query -> {
                    listaProductos.clear();
                    for (DocumentSnapshot doc : query) {
                        Producto p = doc.toObject(Producto.class);
                        if (p.getId() == null) {
                            p.setId(doc.getId()); // asignar el ID de Firestore
                        }
                        listaProductos.add(p);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                });
    }
}