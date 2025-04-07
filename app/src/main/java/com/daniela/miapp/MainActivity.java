package com.daniela.miapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🔥 Conexión a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", "Daniela");
        usuario.put("correo", "daniela@email.com");
        usuario.put("contrasena", "123456");
        usuario.put("rol", "cliente");

        db.collection("usuarios")
                .add(usuario)  // Esto crea un documento nuevo automáticamente
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}