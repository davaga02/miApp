package com.daniela.miapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
                Intent intent = new Intent(ProductosActivity.this, DetalleProductoActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
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
                        productos.add(p);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                );
    }
}
