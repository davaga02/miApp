package com.daniela.miapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.fragment.DetalleProductoFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private List<Producto> productos = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductoAdapter(this, productos, new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onProductoClick(Producto producto) {
                // Abre la pantalla de detalles
                DetalleProductoFragment fragment = new DetalleProductoFragment();
                Bundle args = new Bundle();
                args.putParcelable("producto", producto); // tu clase Producto ya implementa Parcelable
                fragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainer, fragment) // Asegúrate de tener un contenedor con este ID
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(adapter);

        cargarProductos();
    }

    private void cargarProductos() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productos.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Producto p = doc.toObject(Producto.class);

                        if (p != null) {
                            // Validaciones para evitar null y errores
                            if (p.getNombre() == null) p.setNombre("Sin nombre");
                            if (p.getDescripcion() == null) p.setDescripcion("Sin descripción");
                            if (p.getCategoria() == null) p.setCategoria("Sin categoría");
                            if (p.getStock() < 0) p.setStock(0);

                            if (p.getPrecios() == null || p.getPrecios().isEmpty()) {
                                Map<String, Double> preciosDefault = new HashMap<>();
                                preciosDefault.put("único", 0.0);
                                p.setPrecios(preciosDefault);
                            }

                            if (p.getImagenURL() == null) p.setImagenURL("");

                            productos.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                );
    }
}
