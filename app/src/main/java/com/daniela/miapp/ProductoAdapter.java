package com.daniela.miapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_CATEGORIA = 0;
    private static final int TIPO_PRODUCTO = 1;

    private List<Producto> productos;
    private final Context context;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @Override
    public int getItemViewType(int position) {
        return productos.get(position).esCategoria() ? TIPO_CATEGORIA : TIPO_PRODUCTO;
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TIPO_CATEGORIA) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
            return new CategoriaViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
            return new ProductoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        if (holder instanceof CategoriaViewHolder) {
            ((CategoriaViewHolder) holder).tvCategoria.setText(producto.getCategoria());
        } else if (holder instanceof ProductoViewHolder) {
            ProductoViewHolder vh = (ProductoViewHolder) holder;
            vh.tvNombre.setText(producto.getNombre());
            String precioTexto = "";

            if (producto.getPrecios() != null && !producto.getPrecios().isEmpty()) {
                for (Map.Entry<String, Double> entry : producto.getPrecios().entrySet()) {
                    precioTexto += entry.getKey() + ": $" + String.format(Locale.getDefault(), "%.2f", entry.getValue()) + "\n";
                }
            } else {
                precioTexto = "Sin precio";
            }

            vh.tvPrecio.setText(precioTexto.trim());
            Glide.with(context)
                    .load(producto.getImagenURL())
                    .placeholder(R.drawable.placeholder)
                    .into(vh.ivProducto);
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria;

        public CategoriaViewHolder(View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
        }
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre, tvPrecio;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivProducto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}