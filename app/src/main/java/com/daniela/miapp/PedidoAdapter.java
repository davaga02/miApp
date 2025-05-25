package com.daniela.miapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.fragment.CrearPedidoFragment;
import com.google.firebase.firestore.FirebaseFirestore;

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

        switch (pedido.getEstado()) {
            case "Pendiente":
                holder.tvEstado.setTextColor(Color.parseColor("#FFA500")); // naranja
                break;
            case "En preparación":
                holder.tvEstado.setTextColor(Color.parseColor("#007BFF")); // azul
                break;
            case "Completado":
                holder.tvEstado.setTextColor(Color.parseColor("#28a745")); // verde
                break;
            default:
                holder.tvEstado.setTextColor(Color.GRAY);
        }

        holder.tvProductos.setText(productosTexto.toString().trim());
        holder.tvTotalPedido.setText(String.format("Total: %.2f€", total));
        holder.tvCreador.setText("Creado por: " + pedido.getNombreUsuario());


        holder.btnEditar.setOnClickListener(v -> {
            Fragment f = CrearPedidoFragment.newInstance(pedido.getId());
            ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, f)
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnPreparacion.setOnClickListener(v -> {
            actualizarEstadoEnFirestore(pedido.getId(), "En preparación");
        });

        holder.btnCompletado.setOnClickListener(v -> {
            actualizarEstadoEnFirestore(pedido.getId(), "Completado");
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Eliminar Pedido")
                    .setMessage("¿Seguro que querés eliminar este pedido?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("pedidos")
                                .document(pedido.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(holder.itemView.getContext(), "Pedido eliminado", Toast.LENGTH_SHORT).show();
                                    pedidos.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(holder.itemView.getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMesa, tvEstado, tvProductos, tvFechaHora, tvTotalPedido, tvCreador;
        Button btnPreparacion, btnCompletado;
        ImageButton btnEditar, btnEliminar;;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMesa = itemView.findViewById(R.id.tvMesa);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvProductos = itemView.findViewById(R.id.tvProductos);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            tvCreador = itemView.findViewById(R.id.tvCreador);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnPreparacion = itemView.findViewById(R.id.btnEnPreparacion);
            btnCompletado = itemView.findViewById(R.id.btnCompletado);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    public void actualizarPedidos(List<Pedido> nuevos) {
        this.pedidos = nuevos;
        notifyDataSetChanged();
    }

    private void actualizarEstadoEnFirestore(String pedidoId, String nuevoEstado) {
        FirebaseFirestore.getInstance().collection("pedidos")
                .document(pedidoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(unused -> {
                    // Notificar al usuario o refrescar la lista desde afuera si es necesario
                });
    }
}

