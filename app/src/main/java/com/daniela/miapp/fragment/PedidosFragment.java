package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.miapp.Pedido;
import com.daniela.miapp.PedidoAdapter;
import com.daniela.miapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class PedidosFragment extends Fragment {

    private RecyclerView recyclerPedidos;
    private TextView tvSinPedidos;
    private Button btnNuevoPedido;
    private PedidoAdapter adapter;
    private List<Pedido> pedidos = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos);
        tvSinPedidos = view.findViewById(R.id.tvSinPedidos);
        btnNuevoPedido = view.findViewById(R.id.btnNuevoPedido);

        recyclerPedidos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoAdapter(pedidos, pedido -> {
            // Acciones al hacer clic en un pedido, si deseas
        });
        recyclerPedidos.setAdapter(adapter);

        cargarPedidos();

        btnNuevoPedido.setOnClickListener(v -> {
            // Aquí puedes navegar a un Fragment para crear un nuevo pedido, o mostrar un diálogo de selección de mesa
            CrearPedidoFragment crearFragment = new CrearPedidoFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, crearFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void cargarPedidos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("pedidos")
                .whereEqualTo("usuarioId", uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    pedidos.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Pedido p = doc.toObject(Pedido.class);
                        if (p != null) pedidos.add(p);
                    }
                    adapter.setPedidos(pedidos);
                    tvSinPedidos.setVisibility(pedidos.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}
