package com.daniela.miapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Map;

public class DetalleProductoActivity extends AppCompatActivity {

    ImageView ivDetalle;
    TextView tvNombre, tvDescripcion, tvCategoria, tvStock, tvPrecios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        ivDetalle = findViewById(R.id.ivDetalle);
        tvNombre = findViewById(R.id.tvNombre);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvStock = findViewById(R.id.tvStock);
        tvPrecios = findViewById(R.id.tvPrecios);

        Producto producto = (Producto) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            tvNombre.setText(producto.getNombre());
            tvDescripcion.setText(producto.getDescripcion());
            tvCategoria.setText("Categoría: " + producto.getCategoria());
            tvStock.setText("Stock: " + producto.getStock());

            // Mostrar precios (único o por tamaños)
            Map<String, Double> precios = producto.getPrecios();
            if (precios != null && !precios.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Double> entry : precios.entrySet()) {
                    sb.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
                }
                tvPrecios.setText(sb.toString().trim());
            } else {
                tvPrecios.setText("Sin precio");
            }

            Glide.with(this)
                    .load(producto.getImagenURL())
                    .placeholder(R.drawable.placeholder)
                    .into(ivDetalle);
        }
    }
}
