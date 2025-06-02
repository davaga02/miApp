package com.daniela.miapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.PedidoAdapter;
import com.daniela.miapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

        Button btnVerCompletados = view.findViewById(R.id.btnVerCompletados);
        btnVerCompletados.setOnClickListener(v -> cargarPedidosCompletados());

        Button btnVerActivos = view.findViewById(R.id.btnVerActivos);
        btnVerActivos.setOnClickListener(v -> cargarPedidos());

        adapter.setOnPedidoClickListener(pedido -> {
            DetallePedidoFragment fragment = DetallePedidoFragment.newInstance(pedido.getId());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });



    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void cargarPedidos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : "";

        SharedPreferences prefs = requireActivity().getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        String rol = prefs.getString("rol", "empleado");

        db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("pedidos");

        Query query;

        if ("administrador".equals(rol) || "empleado".equals(rol)) {
            query = ref
                    .whereNotEqualTo("estado", "Completado")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            query = ref.whereEqualTo("usuario", uid); // para un cliente, sí filtras por su UID
        }

        query.get()
                .addOnSuccessListener(snapshot -> {
                    listaPedidos.clear();

                    for (DocumentSnapshot doc : snapshot) {
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
                            pedido.setNombreUsuario(nombre); // si tienes este campo en tu modelo
                            pedido.setCorreoUsuario(correo); // si quieres mostrarlo también

                            if (!"Completado".equalsIgnoreCase(pedido.getEstado())) {
                                listaPedidos.add(pedido);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.actualizarPedidos(listaPedidos);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                });
    }



    private void mostrarPedidosFiltrados(List<Pedido> pedidos) {
        // Ordenar por timestamp (más reciente primero)
        pedidos.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

        listaPedidos.clear();
        listaPedidos.addAll(pedidos);
        adapter.actualizarPedidos(listaPedidos);
    }



    private void cargarPedidosCompletados() {
        db.collection("pedidos")
                .whereEqualTo("estado", "Completado")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaPedidos.clear();
                    for (DocumentSnapshot doc : snapshot) {
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
                    Log.e("FIRESTORE_ERROR", "Error al cargar pedidos completados", e); // 👈 Esto muestra el error real en Logcat
                    Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                });
    }
}
