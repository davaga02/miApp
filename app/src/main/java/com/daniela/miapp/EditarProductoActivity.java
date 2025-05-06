package com.daniela.miapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarProductoActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etStock, etPrecio;
    private Button btnGuardar, btnCancelar;
    private Producto producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etStock = findViewById(R.id.etStock);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        producto = (Producto) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            etNombre.setText(producto.getNombre());
            etDescripcion.setText(producto.getDescripcion());
            etStock.setText(String.valueOf(producto.getStock()));
            Double precio = producto.getPrecios().values().iterator().next();
            etPrecio.setText(String.valueOf(precio));
        }

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString();
            String nuevaDesc = etDescripcion.getText().toString();
            int nuevoStock = Integer.parseInt(etStock.getText().toString());
            double nuevoPrecio = Double.parseDouble(etPrecio.getText().toString());

            FirebaseFirestore.getInstance().collection("productos")
                    .whereEqualTo("nombre", producto.getNombre())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (DocumentSnapshot doc : snapshot) {
                            Map<String, Double> nuevosPrecios = new HashMap<>();
                            nuevosPrecios.put("único", nuevoPrecio);

                            doc.getReference().update(
                                    "nombre", nuevoNombre,
                                    "descripcion", nuevaDesc,
                                    "stock", nuevoStock,
                                    "precios", nuevosPrecios
                            ).addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    });
        });

        btnCancelar.setOnClickListener(v -> finish());
    }
}
