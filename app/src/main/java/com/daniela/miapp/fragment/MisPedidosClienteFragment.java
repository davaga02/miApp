package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.PedidoClienteAdapter;
import com.daniela.miapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MisPedidosClienteFragment extends Fragment {

    private RecyclerView recyclerView;
    private PedidoClienteAdapter adapter;
    private List<Pedido> listaPedidos = new ArrayList<>();

    public static Fragment newInstance() {
        return new MisPedidosClienteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false);
        recyclerView = view.findViewById(R.id.recyclerMisPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoClienteAdapter(listaPedidos);
        recyclerView.setAdapter(adapter);
        cargarPedidosCliente();
        return view;
    }

    private void cargarPedidosCliente() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("pedidos")
                .whereEqualTo("usuario", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    listaPedidos.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        try {
                            String id = doc.getId();
                            String usuario = doc.getString("usuario");
                            String correo = doc.getString("correoUsuario");
                            String nombre = doc.getString("nombreUsuario");
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
                            pedido.setNombreUsuario(nombre);
                            pedido.setCorreoUsuario(correo);
                            listaPedidos.add(pedido);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.actualizarPedidos(listaPedidos);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
