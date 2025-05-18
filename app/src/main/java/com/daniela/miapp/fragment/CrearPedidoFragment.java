package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.daniela.miapp.ProductoSeleccionado;
import com.daniela.miapp.R;
import com.daniela.miapp.adapter.ProductoPedidoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class CrearPedidoFragment extends Fragment {

    private Spinner spinnerMesas;
    private RecyclerView recyclerProductos;
    private Button btnConfirmarPedido;
    private SearchView searchView;
    private Spinner spinnerCategorias;
    private List<Producto> listaOriginal = new ArrayList<>();
    private TextView tvTotal;

    private ProductoPedidoAdapter adapter;
    private FirebaseFirestore db;

    private List<Map<String, Object>> productos;
    private List<Producto> listaProductos = new ArrayList<>();
    private Map<String, List<String>> mapaSabores = new HashMap<>();

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

        spinnerCategorias = view.findViewById(R.id.spinnerCategorias);
        searchView = view.findViewById(R.id.searchViewProducto);

        tvTotal = view.findViewById(R.id.tvTotal);


        // Llenar el spinner de mesas (ejemplo: Mesa 1 a Mesa 10)
        List<String> mesas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mesas.add("Mesa " + i);
        }
        ArrayAdapter<String> adapterMesa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, mesas);
        adapterMesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMesas.setAdapter(adapterMesa);


        List<String> categorias = new ArrayList<>();
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categorias);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapterCat);

// Cargar categorías desde Firestore
        db.collection("categorias")
                .get()
                .addOnSuccessListener(snapshot -> {
                    categorias.clear();
                    categorias.add("Todas"); // Opción por defecto

                    for (DocumentSnapshot doc : snapshot) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            categorias.add(nombre);
                        }
                    }

                    adapterCat.notifyDataSetChanged();
                });
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarProductos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarProductos();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarProductos();
                return true;
            }
        });

        // Configurar RecyclerView
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoPedidoAdapter(listaProductos);
        recyclerProductos.setAdapter(adapter);

        cargarSabores(); // ✅ AQUÍ: cargar los sabores desde Firestore
        cargarProductosDesdeFirestore();
        adapter.setOnCambioCantidadListener(this::actualizarTotal);

        btnConfirmarPedido.setOnClickListener(v -> {
            String mesaSeleccionada = (String) spinnerMesas.getSelectedItem();
            String usuarioId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : "empleado";

            List<Map<String, Object>> productosFinal = new ArrayList<>();

            for (ProductoSeleccionado ps : adapter.getSeleccionados().values()) {
                if (ps.getCantidad() > 0) {
                    Producto producto = buscarProductoPorId(ps.getProductoId());
                    if (producto != null) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", producto.getId());
                        item.put("nombre", producto.getNombre());
                        item.put("cantidad", ps.getCantidad());

                        double precioUnitario = 0.0;
                        if (producto.getPrecios() != null) {
                            if (ps.getTamaño() != null && producto.getPrecios().containsKey(ps.getTamaño())) {
                                precioUnitario = producto.getPrecios().get(ps.getTamaño());
                            } else if (producto.getPrecios().containsKey("único")) {
                                precioUnitario = producto.getPrecios().get("único");
                            }
                        }

                        double subtotal = precioUnitario * ps.getCantidad();

                        item.put("precioUnitario", precioUnitario);
                        item.put("subtotal", subtotal);

                        if (ps.getTamaño() != null) item.put("tamaño", ps.getTamaño());
                        if (ps.getSabor() != null) item.put("sabor", ps.getSabor());

                        productosFinal.add(item);
                    }
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

            Button btnVolver = view.findViewById(R.id.btnVolver);
            btnVolver.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
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

    private void filtrarProductos() {
        String categoriaSeleccionada = (String) spinnerCategorias.getSelectedItem();
        String textoBusqueda = searchView.getQuery().toString().toLowerCase();

        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : listaProductos) {
            boolean coincideCategoria = categoriaSeleccionada.equals("Todas") || p.getCategoria().equalsIgnoreCase(categoriaSeleccionada);
            boolean coincideTexto = p.getNombre().toLowerCase().contains(textoBusqueda);
            if (coincideCategoria && coincideTexto) {
                filtrados.add(p);
            }
        }

        adapter.setProductos(filtrados);
        adapter.notifyDataSetChanged();
        actualizarTotal(); // Vuelve a calcular el total con los seleccionados actuales
    }


    private void cargarSabores() {
        db.collection("sabores")
                .get()
                .addOnSuccessListener(snapshot -> {
                    mapaSabores.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        String nombre = doc.getString("nombre");
                        String categoria = doc.getString("categoria");
                        if (nombre != null && categoria != null) {
                            mapaSabores.computeIfAbsent(categoria, k -> new ArrayList<>()).add(nombre);
                        }
                    }
                    adapter.setMapaSabores(mapaSabores);
                });
    }

    private String obtenerNombreProductoPorId(String id) {
        for (Producto p : listaProductos) {
            if (p.getId().equals(id)) {
                return p.getNombre();
            }
        }
        return id; // Fallback
    }

    private Producto buscarProductoPorId(String id) {
        for (Producto p : listaProductos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    private void actualizarTotal() {
        double total = 0.0;

        for (ProductoSeleccionado ps : adapter.getSeleccionados().values()) {
            if (ps.getCantidad() > 0) {
                Producto producto = buscarProductoPorId(ps.getProductoId());
                if (producto != null) {
                    double precioUnitario = 0.0;

                    if (producto.getPrecios() != null) {
                        String tamaño = ps.getTamaño();
                        if (tamaño != null && producto.getPrecios().containsKey(tamaño)) {
                            precioUnitario = producto.getPrecios().get(tamaño);
                        } else if (producto.getPrecios().containsKey("único")) {
                            precioUnitario = producto.getPrecios().get("único");
                        }
                    }

                    total += ps.getCantidad() * precioUnitario;
                }
            }
        }

        tvTotal.setText(String.format("Total: %.2f€", total));
        Log.d("TOTAL_DEBUG", "Productos seleccionados: " + adapter.getSeleccionados().size());
    }
}