package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContrasenaFragment extends Fragment {

    private EditText editActualContrasena, editNuevaContrasena;
    private Button btnGuardarContrasena;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cambiar_contrasena, container, false);

        editActualContrasena = view.findViewById(R.id.editActualContrasena);
        editNuevaContrasena = view.findViewById(R.id.editNuevaContrasena);
        btnGuardarContrasena = view.findViewById(R.id.btnGuardarContrasena);
        mAuth = FirebaseAuth.getInstance();

        btnGuardarContrasena.setOnClickListener(v -> {
            String actualPass = editActualContrasena.getText().toString().trim();
            String nuevaPass = editNuevaContrasena.getText().toString().trim();

            if (actualPass.isEmpty() || nuevaPass.isEmpty()) {
                Toast.makeText(getContext(), "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nuevaPass.length() < 6) {
                Toast.makeText(getContext(), "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), actualPass);

                // 🔐 Reautenticación
                user.reauthenticate(credential)
                        .addOnSuccessListener(unused -> {
                            // ✅ Cambiar contraseña
                            user.updatePassword(nuevaPass)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show());
            }
        });

        ImageButton btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}
