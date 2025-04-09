package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearProductoFragment extends Fragment {

    private EditText etNombre, etDescripcion, etPrecio, etStock;
    private EditText etPrecioPequeno, etPrecioMediano, etPrecioGrande;
    private Spinner spCategoria, spSubcategoria;
    private Switch switchTamanos;
    private LinearLayout layoutTamanos;
    private Button btnCrear;

    private FirebaseFirestore db;

    public CrearProductoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_producto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombreProducto);
        etDescripcion = view.findViewById(R.id.etDescripcionProducto);
        etPrecio = view.findViewById(R.id.etPrecio); // campo precio único
        etStock = view.findViewById(R.id.etStockProducto);

        etPrecioPequeno = view.findViewById(R.id.etPrecioPequeno);
        etPrecioMediano = view.findViewById(R.id.etPrecioMediano);
        etPrecioGrande = view.findViewById(R.id.etPrecioGrande);

        spCategoria = view.findViewById(R.id.spinnerCategoria);
        spSubcategoria = view.findViewById(R.id.spinnerSubcategoria);
        switchTamanos = view.findViewById(R.id.switchTamanos);
        layoutTamanos = view.findViewById(R.id.layoutTamanos);
        btnCrear = view.findViewById(R.id.btnCrearProducto);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> listaCategorias = new ArrayList<>();
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listaCategorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(categoriaAdapter);

        db.collection("categorias").get().addOnSuccessListener(querySnapshot -> {
            listaCategorias.clear();
            listaCategorias.add("Selecciona una categoría");
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                listaCategorias.add(doc.getId());
            }
            categoriaAdapter.notifyDataSetChanged();
        });

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = parent.getItemAtPosition(position).toString();
                if (categoriaSeleccionada.equals("Selecciona una categoría")) return;

                db.collection("categorias")
                        .document(categoriaSeleccionada)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            List<String> subcategorias = (List<String>) documentSnapshot.get("subcategorias");
                            if (subcategorias == null) subcategorias = new ArrayList<>();

                            ArrayAdapter<String> subcatAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subcategorias);
                            subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spSubcategoria.setAdapter(subcatAdapter);

                        });
                if (spCategoria.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Selecciona una categoría válida", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCrear.setOnClickListener(v -> crearProducto());
    }

    private void crearProducto() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();
        String subcategoria = spSubcategoria.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(stockStr)) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("descripcion", descripcion);
        producto.put("stock", stock);
        producto.put("categoria", categoria);
        producto.put("subcategoria", subcategoria);

        if (switchTamanos.isChecked()) {
            String pequeno = etPrecioPequeno.getText().toString().trim();
            String mediano = etPrecioMediano.getText().toString().trim();
            String grande = etPrecioGrande.getText().toString().trim();

            if (TextUtils.isEmpty(pequeno) || TextUtils.isEmpty(mediano) || TextUtils.isEmpty(grande)) {
                Toast.makeText(getContext(), "Introduce todos los precios por tamaño", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> tamanos = new HashMap<>();
            tamanos.put("pequeno", Double.parseDouble(pequeno));
            tamanos.put("mediano", Double.parseDouble(mediano));
            tamanos.put("grande", Double.parseDouble(grande));
            producto.put("tamanos", tamanos);

        } else {
            String precioStr = etPrecio.getText().toString().trim();
            if (TextUtils.isEmpty(precioStr)) {
                Toast.makeText(getContext(), "Introduce el precio del producto", Toast.LENGTH_SHORT).show();
                return;
            }
            producto.put("precio", Double.parseDouble(precioStr));
        }

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Producto creado con éxito", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show());
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        etStock.setText("");
        etPrecioPequeno.setText("");
        etPrecioMediano.setText("");
        etPrecioGrande.setText("");
        switchTamanos.setChecked(false);
        spCategoria.setSelection(0);
        spSubcategoria.setSelection(0);
    }
}
