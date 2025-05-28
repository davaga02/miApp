package com.daniela.miapp.fragment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class SeguimientoPedidoFragment extends Fragment {

    private String pedidoId;
    private TextView tvEstado;
    private ListenerRegistration listener;
    private ActivityResultLauncher<String> permisoNotificacionesLauncher;

    public static SeguimientoPedidoFragment newInstance(String pedidoId) {
        SeguimientoPedidoFragment fragment = new SeguimientoPedidoFragment();
        Bundle args = new Bundle();
        args.putString("pedidoId", pedidoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seguimiento_pedido, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permisoNotificacionesLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        mostrarNotificacionPedidoListo(); // Usuario aceptó
                    } else {
                        Toast.makeText(getContext(), "Permiso de notificación denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvEstado = view.findViewById(R.id.tvEstadoPedido);
        pedidoId = getArguments().getString("pedidoId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listener = db.collection("pedidos").document(pedidoId)
                .addSnapshotListener((doc, error) -> {
                    if (doc != null && doc.exists()) {
                        String estado = doc.getString("estado");
                        tvEstado.setText("Estado actual: " + estado);

                        if ("Completado".equalsIgnoreCase(estado)) {
                            Toast.makeText(getContext(), "🎉 ¡Tu pedido está listo!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Button btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());


    }
    private void mostrarNotificacionPedidoListo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permisoNotificacionesLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }

        // Crea el canal si es necesario
        String canalId = "canal_pedidos";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    canalId, "Pedidos", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), canalId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("¡Pedido Completado!")
                .setContentText("Tu pedido ya está listo 🎉")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(requireContext()).notify(101, builder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listener != null) listener.remove();
    }

}
