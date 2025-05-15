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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.PedidoAdapter;
import com.daniela.miapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidosFragment extends Fragment {

    private RecyclerView recyclerPedidos;
    private PedidoAdapter adapter;
    private FirebaseFirestore db;
    private List<Pedido> listaPedidos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoAdapter(listaPedidos);
        recyclerPedidos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        cargarPedidos();

        Button btnNuevoPedido = view.findViewById(R.id.btnNuevoPedido);
        btnNuevoPedido.setOnClickListener(v -> {
            // Reemplaza con la navegación al fragmento de CrearPedido
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, new CrearPedidoFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void cargarPedidos() {
        db.collection("pedidos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaPedidos.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        try {
                            String id = doc.getId();
                            String usuario = doc.getString("usuario");
                            String mesa = doc.getString("mesa");
                            String estado = doc.getString("estado");
                            Long timestamp = doc.getLong("timestamp");

                            List<Map<String, Object>> productos = new ArrayList<>();
                            Object rawProductos = doc.get("productos");
                            if (rawProductos instanceof List<?>) {
                                for (Object item : (List<?>) rawProductos) {
                                    if (item instanceof Map<?, ?>) {
                                        Map<String, Object> prod = new HashMap<>();
                                        for (Map.Entry<?, ?> entry : ((Map<?, ?>) item).entrySet()) {
                                            if (entry.getKey() instanceof String) {
                                                prod.put((String) entry.getKey(), entry.getValue());
                                            }
                                        }
                                        productos.add(prod);
                                    }
                                }
                            }

                            Pedido pedido = new Pedido(id, usuario, mesa, productos, estado, timestamp);
                            listaPedidos.add(pedido);

                        } catch (Exception e) {
                            e.printStackTrace(); // Muestra el error pero no rompe la app
                        }
                    }
                    adapter.actualizarPedidos(listaPedidos);
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }
}
