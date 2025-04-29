package com.daniela.miapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;

    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        holder.tvStock.setText("Stock: " + producto.getStock());
        holder.tvCategoria.setText(producto.getCategoria() + " - " + producto.getSubcategoria());

        if (producto.getTamanos() != null && !producto.getTamanos().isEmpty()) {
            Map<String, Double> tamanos = producto.getTamanos();
            String precios = "Pequeño: €" + tamanos.get("pequeno") +
                    " | Mediano: €" + tamanos.get("mediano") +
                    " | Grande: €" + tamanos.get("grande");
            holder.tvPrecio.setText(precios);
        } else {
            holder.tvPrecio.setText("Precio: €" + String.format("%.2f", producto.getPrecio()));
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvCategoria, tvPrecio, tvStock;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }
}

