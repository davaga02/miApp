package com.daniela.miapp.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Producto;
import com.daniela.miapp.ProductoSeleccionado;
import com.daniela.miapp.R;

import java.util.*;

public class ProductoPedidoAdapter extends RecyclerView.Adapter<ProductoPedidoAdapter.ViewHolder> {

    private List<Producto> productos;
    private Map<String, ProductoSeleccionado> seleccionados = new HashMap<>();
    private Map<String, List<String>> mapaSabores = new HashMap<>();

    public void setMapaSabores(Map<String, List<String>> mapa) {
        this.mapaSabores = mapa;
        notifyDataSetChanged();
    }

    public ProductoPedidoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    public Map<String, ProductoSeleccionado> getSeleccionados() {
        return seleccionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_pedido, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());

        // Cantidad inicial en 0
        holder.tvCantidad.setText("0");

        // Configurar tamaño si aplica
        if (producto.getPrecios() != null && producto.getPrecios().size() > 1) {
            holder.spinnerTamaño.setVisibility(View.VISIBLE);
            List<String> tamaños = new ArrayList<>(producto.getPrecios().keySet());
            ArrayAdapter<String> adapterTamaño = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, tamaños);
            adapterTamaño.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerTamaño.setAdapter(adapterTamaño);
        } else {
            holder.spinnerTamaño.setVisibility(View.GONE);
        }

        // Configurar sabor si aplica
        if (producto.isRequiereSabor()) {
            holder.spinnerSabor.setVisibility(View.VISIBLE);
            List<String> sabores = mapaSabores.getOrDefault(producto.getCategoria(), new ArrayList<>());
            ArrayAdapter<String> adapterSabor = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, sabores);
            adapterSabor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerSabor.setAdapter(adapterSabor);
        } else {
            holder.spinnerSabor.setVisibility(View.GONE);
        }

        // Botones para sumar/restar cantidad
        holder.btnSumar.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(holder.tvCantidad.getText().toString());
            cantidad++;
            holder.tvCantidad.setText(String.valueOf(cantidad));
        });

        holder.btnRestar.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(holder.tvCantidad.getText().toString());
            if (cantidad > 0) cantidad--;
            holder.tvCantidad.setText(String.valueOf(cantidad));
        });

        // Escucha cambios en cantidad
        holder.tvCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                actualizarSeleccion(producto, holder);
            }
        });

        // Escucha cambios en tamaño
        holder.spinnerTamaño.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                actualizarSeleccion(producto, holder);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Escucha cambios en sabor
        holder.spinnerSabor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                actualizarSeleccion(producto, holder);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Boton agregar al pedido
        holder.btnAgregarProducto.setOnClickListener(v -> {
            int cantidad;
            try {
                cantidad = Integer.parseInt(holder.tvCantidad.getText().toString());
            } catch (NumberFormatException e) {
                cantidad = 0;
            }

            if (cantidad <= 0) {
                Toast.makeText(holder.itemView.getContext(), "⚠️ Cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ya se guarda automáticamente, pero puedes reforzarlo
            actualizarSeleccion(producto, holder);

            Toast.makeText(holder.itemView.getContext(), "✅ Producto añadido correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarSeleccion(Producto producto, ViewHolder holder) {
        int cantidad;
        try {
            cantidad = Integer.parseInt(holder.tvCantidad.getText().toString());
        } catch (NumberFormatException e) {
            cantidad = 0;
        }

        String tamaño = null;
        String sabor = null;

        if (producto.getPrecios() != null && producto.getPrecios().size() > 1) {
            tamaño = (String) holder.spinnerTamaño.getSelectedItem();
        }

        if (producto.isRequiereSabor()) {
            sabor = (String) holder.spinnerSabor.getSelectedItem();
        }

        ProductoSeleccionado seleccionado = new ProductoSeleccionado(producto.getId(), cantidad, tamaño, sabor);
        seleccionados.put(producto.getId(), seleccionado);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad;
        Button btnSumar, btnRestar;
        Spinner spinnerTamaño, spinnerSabor;
        Button btnAgregarProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            spinnerTamaño = itemView.findViewById(R.id.spinnerTamaño);
            spinnerSabor = itemView.findViewById(R.id.spinnerSabor);
            btnAgregarProducto = itemView.findViewById(R.id.btnAgregarProducto);
        }
    }

    public void setProductos(List<Producto> nuevos) {
        this.productos = nuevos;
        notifyDataSetChanged();
    }
}
