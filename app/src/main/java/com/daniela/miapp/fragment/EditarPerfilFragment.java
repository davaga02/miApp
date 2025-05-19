package com.daniela.miapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.LoginActivity;
import com.daniela.miapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText editNombre, editEmail;
    TextView txtRol;
    Button btnGuardar;

    private String nombre, email, rol;

    public static EditarPerfilFragment newInstance(String nombre, String email, String rol) {
        EditarPerfilFragment fragment = new EditarPerfilFragment();
        Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("email", email);
        args.putString("rol", rol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombre = getArguments().getString("nombre");
            email = getArguments().getString("email");
            rol = getArguments().getString("rol");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        editNombre = view.findViewById(R.id.editNombre);
        editEmail = view.findViewById(R.id.editEmail);
        txtRol = view.findViewById(R.id.txtRol);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        // Mostrar datos recibidos (aunque los sobreescribas desde Firebase si prefieres)
        if (nombre != null && email != null && rol != null) {
            editNombre.setText(nombre);
            editEmail.setText(email);
            txtRol.setText("Rol: " + rol);
        }

        // Cargar datos actuales desde Firebase por si están más actualizados
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        editNombre.setText(document.getString("nombre"));
                        editEmail.setText(document.getString("correo")); // CORREGIDO
                        txtRol.setText("Rol: " + document.getString("rol"));
                    }
                });

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = editNombre.getText().toString().trim();
            String nuevoCorreo = editEmail.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            boolean correoCambiado = !nuevoCorreo.equals(user.getEmail());

            // 1. Actualizar en Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("nombre", nuevoNombre);
            updates.put("correo", nuevoCorreo);

            db.collection("usuarios").document(user.getUid()).update(updates)
                    .addOnSuccessListener(unused -> {
                        // 2. Si el correo cambió, actualizarlo en FirebaseAuth y verificar
                        if (correoCambiado) {
                            user.updateEmail(nuevoCorreo)
                                    .addOnSuccessListener(aVoid -> {
                                        user.sendEmailVerification()
                                                .addOnSuccessListener(aVoid2 -> {
                                                    Toast.makeText(getContext(), "Correo actualizado. Verifica el nuevo email.", Toast.LENGTH_LONG).show();

                                                    // 🔐 Cerrar sesión por seguridad
                                                    FirebaseAuth.getInstance().signOut();
                                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                                    requireActivity().finish();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getContext(), "Error al enviar verificación: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Error al actualizar correo: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        } else {
                            // Si no cambió el correo
                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                    });
        });

        ImageButton btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}


