package com.daniela.miapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
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
    private Map<String, ProductoSeleccionado> seleccionados = new HashMap<>();

    private String pedidoId;
    private boolean esEdicion = false;

    public static CrearPedidoFragment newInstance(String pedidoId) {
        CrearPedidoFragment fragment = new CrearPedidoFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedidoId);
        fragment.setArguments(args);
        return fragment;
    }

    public static CrearPedidoFragment newInstanceParaCliente(String mesa) {
        CrearPedidoFragment fragment = new CrearPedidoFragment();
        Bundle args = new Bundle();
        args.putString("mesaCliente", mesa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("pedidoId")) {
            pedidoId = getArguments().getString("pedidoId");
            esEdicion = true;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_pedido, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        spinnerMesas = view.findViewById(R.id.spinnerMesas);

        if (getArguments() != null && getArguments().containsKey("mesaCliente")) {
            String mesaCliente = getArguments().getString("mesaCliente");

            // Esperar a que se llene el spinner antes de seleccionar
            spinnerMesas.post(() -> {
                int index = getIndexMesa(mesaCliente);
                spinnerMesas.setSelection(index);
                spinnerMesas.setEnabled(false);
            });
        }
        recyclerProductos = view.findViewById(R.id.recyclerProductosPedido);

        btnConfirmarPedido = view.findViewById(R.id.btnConfirmarPedido);

        TextView tvTitulo = view.findViewById(R.id.tvTituloCrear);

        if (esEdicion) {
            tvTitulo.setText("Editar Pedido");
            btnConfirmarPedido.setText("Actualizar Pedido");
        }


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

        cargarSaboresEstaticos();
        cargarProductosDesdeFirestore();
        adapter.setOnCambioCantidadListener(this::actualizarTotal);

        btnConfirmarPedido.setOnClickListener(v -> {
            String mesaSeleccionada = (String) spinnerMesas.getSelectedItem();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String usuarioId = currentUser != null ? currentUser.getUid() : "desconocido";
            String correoUsuario = currentUser != null ? currentUser.getEmail() : "sin_correo";

            SharedPreferences prefs = requireContext().getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
            String nombreUsuario = prefs.getString("nombreUsuario", "Desconocido");

            // ✅ Limpiar productos con cantidad 0
            Map<String, ProductoSeleccionado> seleccionadosValidos = new HashMap<>();
            for (Map.Entry<String, ProductoSeleccionado> entry : adapter.getSeleccionados().entrySet()) {
                ProductoSeleccionado ps = entry.getValue();
                if (ps.getCantidad() > 0) {
                    seleccionadosValidos.put(entry.getKey(), ps);
                }
            }
            adapter.setSeleccionados(seleccionadosValidos);

            List<Map<String, Object>> productosFinal = new ArrayList<>();
            for (ProductoSeleccionado ps : seleccionadosValidos.values()) {
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

            Map<String, Object> datosPedido = new HashMap<>();
            datosPedido.put("id", pedido.getId());
            datosPedido.put("usuario", usuarioId);
            datosPedido.put("correoUsuario", correoUsuario);
            datosPedido.put("nombreUsuario", nombreUsuario);
            datosPedido.put("mesa", mesaSeleccionada);
            datosPedido.put("productos", productosFinal);
            datosPedido.put("estado", "Pendiente");
            datosPedido.put("timestamp", System.currentTimeMillis());

            // ✅ Este campo distingue que el pedido lo hizo un cliente
            datosPedido.put("tipo", "cliente");

            if (esEdicion) {
                datosPedido.put("id", pedidoId); // Mantener el mismo ID si se edita
                db.collection("pedidos").document(pedidoId)
                        .set(datosPedido)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(requireContext(), "Pedido actualizado", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
            } else {
                String nuevoId = UUID.randomUUID().toString();
                datosPedido.put("id", nuevoId);

                db.collection("pedidos").document(nuevoId)
                        .set(datosPedido)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(requireContext(), "Pedido creado", Toast.LENGTH_SHORT).show();

                            if (getArguments() != null && getArguments().containsKey("mesaCliente")) {
                                double total = calcularTotal(productosFinal);
                                PagarFragment fragment = PagarFragment.newInstance(nuevoId, total);
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.frameContainer, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "Error al crear pedido", Toast.LENGTH_SHORT).show());
            }
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
                            p.setId(doc.getId());
                        }
                        listaProductos.add(p);
                    }

                    // Crear el adapter una vez que los productos están listos
                    adapter = new ProductoPedidoAdapter(listaProductos);
                    adapter.setMapaSabores(mapaSabores);  // usa sabores si ya se cargaron
                    adapter.setOnCambioCantidadListener(this::actualizarTotal);
                    recyclerProductos.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    actualizarTotal();

                    // 🔁 Si estamos editando, cargar los productos seleccionados ahora
                    if (esEdicion) {
                        cargarPedidoParaEditar(pedidoId);
                    }
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

        // 🔁 IMPORTANTE: actualizar lista sin borrar los seleccionados
        adapter.setProductos(filtrados);
        adapter.notifyDataSetChanged();

        // ✅ NO eliminar productos seleccionados aunque no estén visibles
        actualizarTotal(); // recalcula el total con los productos aún seleccionados
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
                            mapaSabores.computeIfAbsent(categoria.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(nombre);
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



    private void cargarPedidoParaEditar(String id) {
        db.collection("pedidos").document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String mesa = doc.getString("mesa");
                        spinnerMesas.setSelection(getIndexMesa(mesa));

                        List<Map<String, Object>> productosFirestore = (List<Map<String, Object>>) doc.get("productos");

                        Map<String, ProductoSeleccionado> seleccionados = new HashMap<>();

                        for (Map<String, Object> item : productosFirestore) {
                            String nombre = (String) item.get("nombre");
                            String productoId = (String) item.get("id");
                            Long cantidad = (Long) item.get("cantidad");
                            String tamaño = item.containsKey("tamaño") ? (String) item.get("tamaño") : null;
                            String sabor = item.containsKey("sabor") ? (String) item.get("sabor") : null;

                            ProductoSeleccionado ps = new ProductoSeleccionado(productoId);
                            ps.setCantidad(cantidad.intValue());
                            ps.setTamaño(tamaño);
                            ps.setSabor(sabor);
                            seleccionados.put(productoId, ps);
                        }

                        adapter.setSeleccionados(seleccionados);
                        adapter.notifyDataSetChanged();
                        actualizarTotal();
                    }
                });
    }

    private int getIndexMesa(String mesa) {
        for (int i = 0; i < spinnerMesas.getCount(); i++) {
            if (spinnerMesas.getItemAtPosition(i).toString().equalsIgnoreCase(mesa)) {
                return i;
            }
        }
        return 0;
    }

    private void prepararParaEdicion() {
        TextView tvTitulo = requireView().findViewById(R.id.tvTituloCrear);
        btnConfirmarPedido.setText("Actualizar Pedido");
        tvTitulo.setText("Editar Pedido");

    }

    private double calcularTotal(List<Map<String, Object>> productosFinal) {
        double total = 0.0;
        for (Map<String, Object> item : productosFinal) {
            Object subtotalObj = item.get("subtotal");
            if (subtotalObj instanceof Number) {
                total += ((Number) subtotalObj).doubleValue();
            }
        }
        return total;
    }

    private void cargarSaboresYLuegoProductos() {
        db.collection("sabores")
                .get()
                .addOnSuccessListener(snapshot -> {
                    mapaSabores.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        String nombre = doc.getString("nombre");
                        String categoria = doc.getString("categoria");
                        if (nombre != null && categoria != null) {
                            mapaSabores.computeIfAbsent(categoria.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(nombre);
                        }
                    }

                    // Después de tener los sabores, cargar productos
                    cargarProductosDesdeFirestore();  // aquí se cargan los productos
                });
    }

    private void cargarSaboresEstaticos() {
        mapaSabores.clear();

        mapaSabores.put("helados", Arrays.asList(
                "Vainilla", "Chocolate", "Fresa", "Turrón", "Cookies", "Stracciatella", "Pistacho"
        ));

        mapaSabores.put("granizados", Arrays.asList(
                "Limón", "Cola", "Naranja", "Sandía", "Mojito", "Fresa", "Cereza"
        ));

        adapter.setMapaSabores(mapaSabores);
    }



}