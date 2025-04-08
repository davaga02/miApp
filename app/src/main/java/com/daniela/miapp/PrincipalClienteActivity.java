package com.daniela.miapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.fragment.CarritoClienteFragment;
import com.daniela.miapp.fragment.InicioEmpleadoFragment;
import com.daniela.miapp.fragment.MisPedidosClienteFragment;
import com.daniela.miapp.fragment.PerfilClienteFragment;
import com.daniela.miapp.fragment.TiendaClienteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalClienteActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipalcliente);

        bottomNavigation = findViewById(R.id.bottom_navigation_cliente);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_inicio) {
                selectedFragment = new InicioEmpleadoFragment();
            } else if (itemId == R.id.nav_tienda) {
                selectedFragment = new TiendaClienteFragment();
            } else if (itemId == R.id.nav_carrito) {
                selectedFragment = new CarritoClienteFragment();
            }else if (itemId == R.id.nav_pedidos) {
                selectedFragment = new MisPedidosClienteFragment();
            }else if (itemId == R.id.nav_perfil) {
                selectedFragment = new PerfilClienteFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainerCliente, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Fragmento por defecto
        bottomNavigation.setSelectedItemId(R.id.nav_inicio);
    }
}
