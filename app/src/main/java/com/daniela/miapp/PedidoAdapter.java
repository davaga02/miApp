package com.daniela.miapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.fragment.DetallePedidoFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {

    private List<Pedido> pedidos;


    public PedidoAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.tvMesa.setText("Mesa: " + pedido.getMesa());
        holder.tvEstado.setText("Estado: " + pedido.getEstado());

        // ➕ Formatear timestamp a fecha legible

        Date fecha = new Date(pedido.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault());
        holder.tvFechaHora.setText(sdf.format(fecha));

        StringBuilder productosTexto = new StringBuilder();
        double total = 0.0;

        for (Map<String, Object> p : pedido.getProductos()) {
            String nombre = (String) p.get("nombre");
            Long cantidad = (Long) p.get("cantidad");
            String tamaño = (String) p.get("tamaño");
            String sabor = (String) p.get("sabor");

            StringBuilder linea = new StringBuilder("• ")
                    .append(nombre != null ? nombre : p.get("id"))
                    .append(" x").append(cantidad);

            if (tamaño != null) linea.append(" - ").append(tamaño);
            if (sabor != null) linea.append(" - ").append(sabor);

            Object subtotalObj = p.get("subtotal");
            if (subtotalObj instanceof Number) {
                total += ((Number) subtotalObj).doubleValue();
            }

            productosTexto.append(linea).append("\n");
        }
        holder.tvProductos.setText(productosTexto.toString().trim());
        holder.tvTotalPedido.setText(String.format("Total: %.2f€", total));
        holder.tvCreador.setText("Creado por: " + pedido.getNombreUsuario());

        holder.itemView.setOnClickListener(v -> {
            Fragment fragment = DetallePedidoFragment.newInstance(pedido);
            ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMesa, tvEstado, tvProductos, tvFechaHora, tvTotalPedido, tvCreador;;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMesa = itemView.findViewById(R.id.tvMesa);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvProductos = itemView.findViewById(R.id.tvProductos);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            tvCreador = itemView.findViewById(R.id.tvCreador);
        }
    }

    public void actualizarPedidos(List<Pedido> nuevos) {
        this.pedidos = nuevos;
        notifyDataSetChanged();
    }
}

