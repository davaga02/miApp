package com.daniela.miapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.fragment.DetalleProductoFragment;
import com.google.firebase.firestore.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout contenedorCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de que tienes un layout llamado activity_main

        db = FirebaseFirestore.getInstance();
        contenedorCategorias = findViewById(R.id.contenedorCategorias);

        cargarProductosAgrupadosPorCategoria();
    }

    private void cargarProductosAgrupadosPorCategoria() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, List<Producto>> mapa = new HashMap<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        String categoria = doc.getString("categoria");
                        String imagenURL = doc.getString("imagenURL");

                        Map<String, Double> precios = new HashMap<>();
                        Map<String, Object> preciosRaw = (Map<String, Object>) doc.get("precios");
                        if (preciosRaw != null) {
                            for (Map.Entry<String, Object> entry : preciosRaw.entrySet()) {
                                try {
                                    precios.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
                                } catch (Exception e) {
                                    Log.w("ParseError", "Error al convertir precio", e);
                                }
                            }
                        }

                        Producto producto = new Producto(nombre, categoria, precios, imagenURL, false);

                        if (!mapa.containsKey(categoria)) {
                            mapa.put(categoria, new ArrayList<>());
                        }
                        mapa.get(categoria).add(producto);
                    }

                    mostrarCategoriasConProductos(mapa);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error cargando productos", e));
    }

    private void mostrarCategoriasConProductos(Map<String, List<Producto>> mapa) {
        contenedorCategorias.removeAllViews();

        for (Map.Entry<String, List<Producto>> entry : mapa.entrySet()) {
            String categoria = entry.getKey();
            List<Producto> productos = entry.getValue();

            // Título de la categoría
            TextView titulo = new TextView(this);
            titulo.setText(categoria);
            titulo.setTextSize(20f);
            titulo.setTextColor(getResources().getColor(R.color.teal_700));
            titulo.setPadding(24, 32, 0, 8);
            titulo.setTypeface(null, android.graphics.Typeface.BOLD);
            contenedorCategorias.addView(titulo);

            // RecyclerView horizontal para productos
            RecyclerView rv = new RecyclerView(this);
            rv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            ProductoAdapter adapter = new ProductoAdapter(MainActivity.this, productos, new ProductoAdapter.OnProductoClickListener() {
                @Override
                public void onProductoClick(Producto producto) {
                    DetalleProductoFragment fragment = new DetalleProductoFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("producto", producto); // ✅ Usar Parcelable

                    fragment.setArguments(args);

                    ((AppCompatActivity) MainActivity.this).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            rv.setAdapter(adapter);
            contenedorCategorias.addView(rv);
        }
    }
}

