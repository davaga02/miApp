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

        etNombreUs = findViewById(R.id.email);
        etContrasena = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        SignInButton btnGoogle = findViewById(R.id.btnGoogleSignIn);
        tvRegistro = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();

        // 🔐 Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576438427027-ns3dui7upkr9s22mmn34ct6d0165dkqm.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // ✅ Verifica si hay una sesión previa
        GoogleSignInAccount lastSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastSignInAccount != null) {
            Log.d("LOGIN_GOOGLE", "Última sesión: " + lastSignInAccount.getEmail());
        } else {
            Log.d("LOGIN_GOOGLE", "Ninguna sesión anterior");
        }

        // 📲 Nuevo método recomendado en lugar de startActivityForResult
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("LOGIN_GOOGLE", "Resultado recibido del launcher");

                    Intent data = result.getData();

                    if (data == null) {
                        Log.e("LOGIN_GOOGLE", "data == null");
                    } else {
                        Log.d("LOGIN_GOOGLE", "Intent data: " + data.toUri(0));
                    }

                    if (result.getResultCode() == RESULT_OK && data != null) {
                        Log.d("LOGIN_GOOGLE", "Intent OK, extrayendo cuenta...");
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    } else {
                        Log.e("LOGIN_GOOGLE", "Sign-In cancelado o sin datos");
                        Toast.makeText(this, "Login cancelado o fallido", Toast.LENGTH_SHORT).show();
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
                        // OBTENEMOS nombre y rol
                        String rol = queryDocumentSnapshots.getDocuments().get(0).getString("rol");
                        String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("nombre");

                        Toast.makeText(this, "Bienvenido " + rol, Toast.LENGTH_SHORT).show();

                        Intent intent = null;

                        if ("administrador".equals(rol)) {
                            intent = new Intent(this, PrincipalAdminActivity.class);
                        } else if ("empleado".equals(rol)) {
                            intent = new Intent(this, PrincipalEmpleadoActivity.class);
                        } else {
                            intent = new Intent(this, PrincipalClienteActivity.class);
                        }

                        // PASAMOS EL NOMBRE por el intent
                        intent.putExtra("nombreUsuario", nombre);
                        startActivity(intent);

                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al conectar", Toast.LENGTH_SHORT).show());
    }

    // ✅ Resultado del inicio de sesión con Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d("LOGIN_GOOGLE", "Entró en handleSignInResult");

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("LOGIN_GOOGLE", "Cuenta obtenida: " + account.getEmail());

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d("LOGIN_GOOGLE", "Autenticado como: " + user.getEmail());
                                guardarUsuarioFirestore(
                                        user.getUid(),
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        "cliente"
                                );

                                Toast.makeText(this, "Bienvenido " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, PrincipalClienteActivity.class));
                                finish();
                            }
                        } else {
                            Log.e("LOGIN_GOOGLE", "Error en signInWithCredential", task.getException());
                            Toast.makeText(this, "Error al autenticar", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (ApiException e) {
            Log.e("LOGIN_GOOGLE", "ApiException: código = " + e.getStatusCode(), e);
            Toast.makeText(this, "Fallo login: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
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
        // ir a la actividad de registro
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}