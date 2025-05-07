package com.daniela.miapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.AdapterCategoria;
import com.daniela.miapp.Producto;
import com.daniela.miapp.ProductoAdapter;
import com.daniela.miapp.R;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment {

    private RecyclerView rvProductos, rvCategorias;
    private ProductoAdapter adapterProductos;
    private AdapterCategoria adapterCategoria;
    private List<Producto> listaProductos = new ArrayList<>();
    private List<String> listaCategorias = new ArrayList<>();

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        rvProductos = view.findViewById(R.id.rvProductos);
        rvCategorias = view.findViewById(R.id.rvCategorias);

        rvProductos.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columnas;
        adapterProductos = new ProductoAdapter((AppCompatActivity) requireActivity(), listaProductos, producto -> {
            DetalleProductoFragment fragment = new DetalleProductoFragment();
            Bundle args = new Bundle();
            args.putParcelable("producto", producto); // ESTO es correcto
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvProductos.setAdapter(adapterProductos);
        rvProductos.setAdapter(adapterProductos);

        rvCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterCategoria = new AdapterCategoria(listaCategorias, categoriaSeleccionada -> {
            if (categoriaSeleccionada.equals("Todos")) {
                adapterProductos.setProductos(listaProductos); // Sin filtro
            } else {
                List<Producto> filtrados = new ArrayList<>();
                for (Producto p : listaProductos) {
                    if (p.getCategoria().equals(categoriaSeleccionada)) {
                        filtrados.add(p);
                    }
                }
                adapterProductos.setProductos(filtrados);
            }
            adapterProductos.notifyDataSetChanged();
        });
        rvCategorias.setAdapter(adapterCategoria);

        cargarCategorias();
        cargarProductos();

        return view;
    }

    private void cargarCategorias() {
        FirebaseFirestore.getInstance().collection("categorias")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaCategorias.clear();
                    listaCategorias.add("Todos");
                    for (DocumentSnapshot doc : snapshot) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) listaCategorias.add(nombre);
                    }
                    adapterCategoria.notifyDataSetChanged();
                });
    }

    private void cargarProductos() {
        FirebaseFirestore.getInstance().collection("productos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaProductos.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Producto producto = doc.toObject(Producto.class);

                        // ✅ Validación para asegurar que siempre tenga al menos un precio
                        if (producto != null && (producto.getPrecios() == null || producto.getPrecios().isEmpty())) {
                            Map<String, Double> preciosDefault = new HashMap<>();
                            preciosDefault.put("único", 0.0);
                            producto.setPrecios(preciosDefault);
                        }
                        if (producto != null) listaProductos.add(producto);
                    }
                    adapterProductos.setProductos(listaProductos); // Muestra todos por defecto
                    adapterProductos.notifyDataSetChanged();
                });
    }

}


