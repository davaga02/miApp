package com.daniela.miapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Producto;
import com.daniela.miapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoPedidoAdapter extends RecyclerView.Adapter<ProductoPedidoAdapter.ViewHolder> {

    private List<Producto> productos;
    private Map<String, Integer> cantidades = new HashMap<>();

    public ProductoPedidoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    public Map<String, Integer> getCantidadesSeleccionadas() {
        return cantidades;
    }

    @NonNull
    @Override
    public ProductoPedidoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_pedido, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoPedidoAdapter.ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());

        holder.etCantidad.setText("0");
        cantidades.put(producto.getId(), 0);

        holder.etCantidad.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int cantidad = Integer.parseInt(holder.etCantidad.getText().toString());
                    if (cantidad < 0) cantidad = 0;
                    cantidades.put(producto.getId(), cantidad);
                } catch (NumberFormatException e) {
                    holder.etCantidad.setText("0");
                    cantidades.put(producto.getId(), 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        EditText etCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            etCantidad = itemView.findViewById(R.id.etCantidadProducto);
        }
    }
}
