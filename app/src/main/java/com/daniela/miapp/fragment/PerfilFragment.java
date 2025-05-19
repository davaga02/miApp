package com.daniela.miapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.daniela.miapp.LoginActivity;
import com.daniela.miapp.R;

public class PerfilFragment extends Fragment {

    private TextView txtNombre, txtEmail, txtRol;
    private Button btnEditarPerfil, btnCambiarContrasena, btnCerrarSesion;

    private String nombre, email, rol;

    public PerfilFragment() {}

    public static PerfilFragment newInstance(String nombre, String email, String rol) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("email", email);
        args.putString("rol", rol);
        fragment.setArguments(args);
        return fragment;
    }

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

        if (getArguments() != null) {
            nombre = getArguments().getString("nombre");
            email = getArguments().getString("email");
            rol = getArguments().getString("rol");
        }

        txtNombre.setText("Nombre: " + nombre);
        txtEmail.setText("Email: " + email);
        txtRol.setText("Rol: " + rol);

        btnEditarPerfil.setOnClickListener(v -> {
            // Aquí iría navegación a EditarPerfilActivity o Fragment
            Toast.makeText(getContext(), "Editar perfil", Toast.LENGTH_SHORT).show();
        });

        btnCambiarContrasena.setOnClickListener(v -> {
            // Aquí iría navegación a CambiarContraseñaActivity o Fragment
            Toast.makeText(getContext(), "Cambiar contraseña", Toast.LENGTH_SHORT).show();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            // Aquí borras datos de sesión (ejemplo usando SharedPreferences)
            SharedPreferences preferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            preferences.edit().clear().apply();

            // Regresar a LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
