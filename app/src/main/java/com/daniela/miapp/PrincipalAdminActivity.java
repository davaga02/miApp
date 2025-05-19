package com.daniela.miapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daniela.miapp.fragment.EmployeesFragment;
import com.daniela.miapp.fragment.HomeFragment;
import com.daniela.miapp.fragment.OrdersFragment;
import com.daniela.miapp.fragment.PerfilFragment;
import com.daniela.miapp.fragment.ProductsFragment;
import com.daniela.miapp.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalAdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    String nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipaladmin);


        SharedPreferences prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE);
        nombre = getIntent().getStringExtra("nombreUsuario");

        if (!prefs.getBoolean("logueado", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (nombre == null || nombre.isEmpty()) {
            nombre = prefs.getString("nombreUsuario", "Administrador");
        }

        TextView tvAppName = findViewById(R.id.tvAppName);
        if (tvAppName != null) {
            tvAppName.setText("Hola " + nombre);
        }

        // Este código puede ir en el método onCreate o al seleccionar "Perfil"
        String nombre = "Nombre del usuario";
        String email = "correo@ejemplo.com";
        String rol = "admin"; // o "empleado", puedes obtenerlo desde SharedPreferences o Firebase

        PerfilFragment fragment = PerfilFragment.newInstance(nombre, email, rol);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment) // fragment_container = tu contenedor de fragments
                .commit();

        initView();
        initValues();
        initListener();
    }

    // Inicializa las vistas
    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    // Configura el fragmento inicial
    private void initValues() {
        fragmentManager = getSupportFragmentManager();
        loadFirstFragment();
    }

    // Configura el Listener del BottomNavigationView
    private void initListener() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId(); // Get the item ID

                if (itemId == R.id.nav_home) {
                    selectedFragment = HomeFragment.newInstance();
                } else if (itemId == R.id.nav_orders) {
                    selectedFragment = OrdersFragment.newInstance();
                } else if (itemId == R.id.nav_products) {
                    selectedFragment = ProductsFragment.newInstance();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = ProfileFragment.newInstance();
                }

                // Cargar el fragmento correspondiente
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameContainer, selectedFragment)
                            .addToBackStack(null)  // Permite navegar hacia atrás entre fragmentos
                            .commit();
                }

                return true; // Indica que el ítem fue seleccionado
            }
        });
    }

    // Método para cargar el primer fragmento
    private void loadFirstFragment() {
        fragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .commit();
    }
}