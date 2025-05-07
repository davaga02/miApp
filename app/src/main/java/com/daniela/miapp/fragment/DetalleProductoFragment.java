package com.daniela.miapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.daniela.miapp.Producto;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.daniela.miapp.Producto;
import com.daniela.miapp.R;
import com.google.firebase.firestore.*;

import java.util.Map;

public class DetalleProductoFragment extends Fragment {
    private ImageView ivDetalle;
    private TextView tvNombre, tvDescripcion, tvCategoria, tvStock, tvPrecios;
    private Button btnEditar, btnEliminar, btnVolver;
    private Producto producto;

    public DetalleProductoFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_detalle_producto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivDetalle = view.findViewById(R.id.ivDetalle);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvCategoria = view.findViewById(R.id.tvCategoria);
        tvStock = view.findViewById(R.id.tvStock);
        tvPrecios = view.findViewById(R.id.tvPrecios);

        btnEditar = view.findViewById(R.id.btnEditar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnVolver = view.findViewById(R.id.btnVolver);

        if (getArguments() != null) {
            producto = getArguments().getParcelable("producto");

            if (producto != null) {
                tvNombre.setText(producto.getNombre());
                tvDescripcion.setText(producto.getDescripcion());
                tvCategoria.setText("Categoría: " + producto.getCategoria());
                tvStock.setText("Stock: " + producto.getStock());

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

                if (producto.getImagenURL() != null && !producto.getImagenURL().isEmpty()) {
                    Glide.with(requireContext())
                            .load(producto.getImagenURL())
                            .placeholder(R.drawable.placeholder)
                            .into(ivDetalle);
                } else {
                    ivDetalle.setImageResource(R.drawable.placeholder);
                }
            }
        }

        btnEditar.setOnClickListener(v -> {
            Log.d("EditarProductoFragment", "Producto recibido: " + producto);
            EditarProductoFragment editarFragment = new EditarProductoFragment();
            Bundle args = new Bundle();
            args.putParcelable("producto", producto);
            editarFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });

        Context context = requireContext(); // o "this" si estás en Activity
        ContextThemeWrapper themedContext = new ContextThemeWrapper(context, R.style.CustomAlertDialog);
        btnEliminar.setOnClickListener(v -> {
        new AlertDialog.Builder(themedContext)
                .setTitle("¿Eliminar producto?")
                .setMessage("¿Estás segura/o de que deseas eliminar este producto?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    FirebaseFirestore.getInstance()
                            .collection("productos")
                            .whereEqualTo("nombre", producto.getNombre())
                            .get()
                            .addOnSuccessListener(query -> {
                                for (DocumentSnapshot doc : query) {
                                    doc.getReference().delete();
                                }
                                Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });


        btnVolver.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }
}

