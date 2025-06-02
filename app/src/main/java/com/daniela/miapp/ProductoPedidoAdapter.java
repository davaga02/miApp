package com.daniela.miapp.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniela.miapp.Producto;
import com.daniela.miapp.ProductoSeleccionado;
import com.daniela.miapp.R;

import java.util.*;

public class ProductoPedidoAdapter extends RecyclerView.Adapter<ProductoPedidoAdapter.ViewHolder> {

    private List<Producto> productos;
    private Map<String, ProductoSeleccionado> seleccionados = new HashMap<>();
    private Map<String, List<String>> mapaSabores = new HashMap<>();


    public void setMapaSabores(Map<String, List<String>> mapa) {
        this.mapaSabores = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : mapa.entrySet()) {
            this.mapaSabores.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        notifyDataSetChanged();
    }


    public ProductoPedidoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    public Map<String, ProductoSeleccionado> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(Map<String, ProductoSeleccionado> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public interface OnCambioCantidadListener {
        void onCambio();
    }

    private OnCambioCantidadListener listener;

    public void setOnCambioCantidadListener(OnCambioCantidadListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_pedido, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.spinnerTamaño.setOnItemSelectedListener(null);
        holder.spinnerSabor.setOnItemSelectedListener(null);
        holder.tvCantidad.setText("");

        Producto producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());

        // Buscar selección previa
        ProductoSeleccionado psGuardado = buscarSeleccionPorProducto(producto);
        int cantidadGuardada = psGuardado != null ? psGuardado.getCantidad() : 0;
        holder.tvCantidad.setText(String.valueOf(Math.max(0, cantidadGuardada)));

        // Tamaño
        if (producto.getPrecios() != null && producto.getPrecios().size() > 1) {
            holder.spinnerTamaño.setVisibility(View.VISIBLE);
            List<String> tamaños = new ArrayList<>(producto.getPrecios().keySet());
            ArrayAdapter<String> adapterTamaño = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_spinner_item, tamaños);
            adapterTamaño.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerTamaño.setAdapter(adapterTamaño);

            if (psGuardado != null && psGuardado.getTamaño() != null) {
                int index = adapterTamaño.getPosition(psGuardado.getTamaño());
                if (index >= 0) holder.spinnerTamaño.setSelection(index);
            }

        } else {
            holder.spinnerTamaño.setVisibility(View.GONE);
        }

        // Sabor
        if (producto.isRequiereSabor()) {
            holder.spinnerSabor.setVisibility(View.VISIBLE);

            String categoriaKey = producto.getCategoria() != null ? producto.getCategoria().toLowerCase(Locale.ROOT) : "";
            List<String> sabores = mapaSabores.getOrDefault(categoriaKey, new ArrayList<>());

            ArrayAdapter<String> adapterSabor = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_spinner_item, sabores);
            adapterSabor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerSabor.setAdapter(adapterSabor);

            if (psGuardado != null && psGuardado.getSabor() != null) {
                int index = adapterSabor.getPosition(psGuardado.getSabor());
                if (index >= 0) holder.spinnerSabor.setSelection(index);
            }

        } else {
            holder.spinnerSabor.setVisibility(View.GONE);
        }

        // Botones
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

        // Precio
        Double precio = 0.0;
        if (producto.getPrecios() != null) {
            if (producto.getPrecios().containsKey("único")) {
                precio = producto.getPrecios().get("único");
            } else if (!producto.getPrecios().isEmpty()) {
                precio = new ArrayList<>(producto.getPrecios().values()).get(0);
            }
        }
        holder.tvPrecioProducto.setText(String.format("%.2f€", precio));

        // Imagen
        if (producto.getImagenURL() != null && !producto.getImagenURL().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(producto.getImagenURL())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.placeholder);
        }

        // Agregar producto al mapa
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

            if (listener != null) {
                listener.onCambio();
            }

            Toast.makeText(holder.itemView.getContext(), "✅ Producto añadido correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    /*
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
        if (listener != null) {
            listener.onCambio();
        }

        ProductoSeleccionado seleccionado = new ProductoSeleccionado(producto.getId(), cantidad, tamaño, sabor);
        seleccionados.put(producto.getId(), seleccionado);
    }

     */

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad;
        ImageButton btnSumar, btnRestar;
        Spinner spinnerTamaño, spinnerSabor;
        Button btnAgregarProducto;
        TextView tvPrecioProducto;
        ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            spinnerTamaño = itemView.findViewById(R.id.spinnerTamaño);
            spinnerSabor = itemView.findViewById(R.id.spinnerSabor);
            btnAgregarProducto = itemView.findViewById(R.id.btnAgregarProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }

    public void setProductos(List<Producto> nuevos) {
        this.productos = nuevos;
        notifyDataSetChanged();
    }
    private ProductoSeleccionado buscarSeleccionPorProducto(Producto producto) {
        for (ProductoSeleccionado ps : seleccionados.values()) {
            if (ps.getProductoId().equals(producto.getId())) {
                return ps;
            }
        }
        return null;
    }
}
