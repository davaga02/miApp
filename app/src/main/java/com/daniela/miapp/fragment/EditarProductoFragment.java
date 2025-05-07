package com.daniela.miapp.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.Producto;
import com.daniela.miapp.R;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class EditarProductoFragment extends Fragment {

    private EditText etNombre, etDescripcion, etStock, etPrecio;
    private Button btnGuardar, btnCancelar;
    private Producto producto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_editar_producto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etNombre = view.findViewById(R.id.etNombre);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etStock = view.findViewById(R.id.etStock);
        etPrecio = view.findViewById(R.id.etPrecio);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        producto = requireArguments().getParcelable("producto");

        if (producto != null) {
            etNombre.setText(producto.getNombre());
            etDescripcion.setText(producto.getDescripcion());
            etStock.setText(String.valueOf(producto.getStock()));

            if (producto.getPrecios() == null || producto.getPrecios().isEmpty()) {
                Map<String, Double> preciosPorDefecto = new HashMap<>();
                preciosPorDefecto.put("único", 0.0);
                producto.setPrecios(preciosPorDefecto);
            }

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
                                Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed(); // Vuelve atrás
                            });
                        }
                    });
        });

        btnCancelar.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}