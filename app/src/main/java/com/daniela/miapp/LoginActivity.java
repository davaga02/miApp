package com.daniela.miapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etNombreUs, etContrasena;
    private Button btnLogin, btnGoogle;
    private TextView tvRegistro;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNombreUs = findViewById(R.id.etUsername);
        etContrasena = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        SignInButton btnGoogle = findViewById(R.id.btnGoogleSignIn);
        tvRegistro = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();

        // 🔐 Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576438427027-n9q2g3e77de4u0cfm81oao30gdd96hs3.apps.googleusercontent.com") // 👈 Web Client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 📲 Nuevo método recomendado en lugar de startActivityForResult
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                });

        // 👉 Evento del botón de Google
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    // 🔐 Login con email y contraseña (simulado)
    public void onClikAcceder(View view) {
        String correo = etNombreUs.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .whereEqualTo("correo", correo)
                .whereEqualTo("contrasena", contrasena)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String rol = queryDocumentSnapshots.getDocuments().get(0).getString("rol");

                        Toast.makeText(this, "Bienvenido " + rol, Toast.LENGTH_SHORT).show();

                        if ("administrador".equals(rol)) {
                            startActivity(new Intent(this, PrincipalAdminActivity.class));
                        } else if ("empleado".equals(rol)) {
                            startActivity(new Intent(this, PrincipalEmpleadoActivity.class));
                        } else {
                            startActivity(new Intent(this, PrincipalClienteActivity.class));
                        }

                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al conectar", Toast.LENGTH_SHORT).show());
    }

    // ✅ Resultado del inicio de sesión con Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // 👉 Aquí se asigna automáticamente el rol de "cliente"
                                guardarUsuarioFirestore(
                                        user.getUid(),
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        "cliente"
                                );

                                Toast.makeText(this, "Bienvenido " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, PrincipalClienteActivity.class));
                            }
                        } else {
                            Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "Error: " + e.getStatusCode());
            Toast.makeText(this, "Fallo el inicio con Google", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Guarda usuario en Firestore
    private void guardarUsuarioFirestore(String uid, String nombre, String correo, String rol) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("correo", correo);
        datos.put("rol", rol);

        db.collection("usuarios").document(uid)
                .set(datos, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario guardado"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar", e));
    }

    public void onClickRegistrar(View view) {
        // Opcional: ir a la actividad de registro
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}