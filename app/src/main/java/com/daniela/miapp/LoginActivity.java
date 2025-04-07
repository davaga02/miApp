package com.daniela.miapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etNombreUs, etContrasena;
    private Button btnLogin;
    private TextView tvRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etNombreUs = findViewById(R.id.etUsername);
        etContrasena = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistro = findViewById(R.id.tvRegister);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClikAcceder(View view) {
        String usuario = etNombreUs.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        Intent intent = new Intent(LoginActivity.this, PrincipalAdminActivity.class);
        startActivity(intent);

/*
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un nombre de usuario y una contraseña", Toast.LENGTH_SHORT).show();
            return;

        }else {
            // Aquí puedes integrar la autenticación con Spring Boot a través de Retrofit o Volley
            // Si la autenticación es exitosa, redirigir al administrador a la pantalla principal
            Toast.makeText(LoginActivity.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
            // Ejemplo: Intent para ir a la página principal del administrador
            // startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
        }

 */
    }

    public void onClickRegistrar(View view) {
        // Redirige al usuario a la actividad de registro
    }
}

