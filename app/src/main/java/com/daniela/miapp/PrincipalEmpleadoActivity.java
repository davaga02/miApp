
package com.daniela.miapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.fragment.InicioEmpleadoFragment;
import com.daniela.miapp.fragment.PedidosFragment;
import com.daniela.miapp.fragment.PerfilEmpleadoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalEmpleadoActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipalempleado);

        String nombre = getIntent().getStringExtra("nombreUsuario");
        TextView tvAppName = findViewById(R.id.tvAppName);

        if (tvAppName != null && nombre != null) {
            tvAppName.setText("Hola " + nombre);
        }

        bottomNavigation = findViewById(R.id.bottom_navigation_empleado);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_inicio) {
                selectedFragment = new InicioEmpleadoFragment();
            } else if (itemId == R.id.nav_pedidos) {
                selectedFragment = new PedidosFragment();
            } else if (itemId == R.id.nav_perfil) {
                selectedFragment = new PerfilEmpleadoFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainer, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Fragmento por defecto
        bottomNavigation.setSelectedItemId(R.id.nav_inicio);
    }
}
