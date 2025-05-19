
package com.daniela.miapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.fragment.InicioEmpleadoFragment;
import com.daniela.miapp.fragment.PedidosFragment;
import com.daniela.miapp.fragment.PerfilEmpleadoFragment;
import com.daniela.miapp.fragment.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalEmpleadoActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipalempleado);

        String n = getIntent().getStringExtra("nombreUsuario");
        TextView tvAppName = findViewById(R.id.tvAppName);

        // Este código puede ir en el método onCreate o al seleccionar "Perfil"
        String nombre = "Nombre del usuario";
        String email = "correo@ejemplo.com";
        String rol = "admin"; // o "empleado", puedes obtenerlo desde SharedPreferences o Firebase

        PerfilFragment fragment = PerfilFragment.newInstance(nombre, email, rol);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment) // fragment_container = tu contenedor de fragments
                .commit();

        if (tvAppName != null && n != null) {
            tvAppName.setText("Hola " + n);
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
