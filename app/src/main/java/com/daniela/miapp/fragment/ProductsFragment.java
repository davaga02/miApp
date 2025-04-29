package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Producto;
import com.daniela.miapp.ProductoAdapter;
import com.daniela.miapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnAgregar;

    public  ProductsFragment() {
        // Constructor vacío
    }

    public static ProductsFragment newInstance() {

        Bundle args = new Bundle();

        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_products, container, false);

        /*View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = view.findViewById(R.id.recyclerProductos);
        btnAgregar = view.findViewById(R.id.btnAgregar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarProductos();

        btnAgregar.setOnClickListener(v -> {
            // Cambia al fragmento de crear producto
            //Fragment crearFragment = new CrearProductoFragment();
            //getActivity().getSupportFragmentManager()
                    //.beginTransaction()
                    //.replace(R.id.frameContainer, crearFragment)
                    //.addToBackStack(null)
                    //.commit();
        });

        return view;

         */
    }
/*
    private void cargarProductos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Producto p = doc.toObject(Producto.class);
                        productos.add(p);
                    }
                    recyclerView.setAdapter(new ProductoAdapter(productos));
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show());
    }

 */

}
