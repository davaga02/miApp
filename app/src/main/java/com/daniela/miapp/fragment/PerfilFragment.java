package com.daniela.miapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.daniela.miapp.LoginActivity;
import com.daniela.miapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilFragment extends Fragment {

    private TextView txtNombre, txtEmail, txtRol;
    private Button btnEditarPerfil, btnCambiarContrasena, btnCerrarSesion;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String nombre, email, rol;

    public PerfilFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        txtNombre = view.findViewById(R.id.txtNombre);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtRol = view.findViewById(R.id.txtRol);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnCambiarContrasena = view.findViewById(R.id.btnCambiarContrasena);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        nombre = document.getString("nombre");
                        email = document.getString("correo"); // corregido de "email"
                        rol = document.getString("rol");

                        txtNombre.setText("Nombre: " + nombre);
                        txtEmail.setText("Email: " + email);
                        txtRol.setText("Rol: " + rol);

                        // Listener para editar (solo cuando ya tenemos los datos)
                        btnEditarPerfil.setOnClickListener(v -> {
                            Fragment editarPerfilFragment = EditarPerfilFragment.newInstance(nombre, email, rol);
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameContainer, editarPerfilFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                );

        // Cambiar contraseña
        btnCambiarContrasena.setOnClickListener(v -> {
            Fragment cambiarFragment = new CambiarContrasenaFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, cambiarFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
            preferences.edit().clear().apply();

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
