package com.daniela.miapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class PedidoClienteAdapter extends RecyclerView.Adapter<PedidoClienteAdapter.ViewHolder> {

    private List<Pedido> listaPedidos;

    public PedidoClienteAdapter(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_cliente, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        // Fecha
        Date fecha = new Date(pedido.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault());
        holder.tvFechaHora.setText(sdf.format(fecha));

        // Productos
        StringBuilder productosTexto = new StringBuilder();
        double total = 0.0;

        for (Map<String, Object> prod : pedido.getProductos()) {
            String nombre = (String) prod.get("nombre");
            Long cantidad = (Long) prod.get("cantidad");
            String tamaño = (String) prod.get("tamaño");
            String sabor = (String) prod.get("sabor");
            double subtotal = prod.get("subtotal") instanceof Number ?
                    ((Number) prod.get("subtotal")).doubleValue() : 0;

            total += subtotal;

            productosTexto.append("• ")
                    .append(nombre != null ? nombre : "Producto")
                    .append(" x").append(cantidad);
            if (tamaño != null) productosTexto.append(" - ").append(tamaño);
            if (sabor != null) productosTexto.append(" - ").append(sabor);
            productosTexto.append("\n");
        }

        holder.tvProductos.setText(productosTexto.toString().trim());
        holder.tvTotal.setText(String.format("Total: %.2f€", total));
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHora, tvProductos, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvProductos = itemView.findViewById(R.id.tvProductosCliente);
            tvTotal = itemView.findViewById(R.id.tvTotalCliente);
        }
    }

    public void actualizarPedidos(List<Pedido> nuevos) {
        this.listaPedidos = nuevos;
        notifyDataSetChanged();
    }
}

