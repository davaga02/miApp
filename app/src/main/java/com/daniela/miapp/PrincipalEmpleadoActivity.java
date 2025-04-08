
package com.daniela.miapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.fragment.InicioEmpleadoFragment;
import com.daniela.miapp.fragment.PedidosEmpleadoFragment;
import com.daniela.miapp.fragment.PerfilEmpleadoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalEmpleadoActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipalempleado);

        bottomNavigation = findViewById(R.id.bottom_navigation_empleado);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_inicio) {
                selectedFragment = new InicioEmpleadoFragment();
            } else if (itemId == R.id.nav_pedidos) {
                selectedFragment = new PedidosEmpleadoFragment();
            } else if (itemId == R.id.nav_perfil) {
                selectedFragment = new PerfilEmpleadoFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainerEmpleado, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Fragmento por defecto
        bottomNavigation.setSelectedItemId(R.id.nav_inicio);
    }
}
