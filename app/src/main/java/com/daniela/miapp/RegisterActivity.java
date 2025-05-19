package com.daniela.miapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etContrasena;
    private Spinner spinnerRol;
    private Button btnRegistrar;
    private TextView tvVolver;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolver = findViewById(R.id.tvVolver);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        tvVolver.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString().toLowerCase();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnSuccessListener(unused -> Toast.makeText(this, "Correo de verificación enviado", Toast.LENGTH_LONG).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show());

                            String uid = user.getUid();

                            // Guardar en Firestore
                            Map<String, Object> datos = new HashMap<>();
                            datos.put("nombre", nombre);
                            datos.put("correo", correo);
                            datos.put("rol", rol);

                            db.collection("usuarios").document(uid).set(datos)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Usuario registrado. Verifica tu correo.", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut(); // cerrar sesión
                                        startActivity(new Intent(this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}