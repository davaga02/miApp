package com.daniela.miapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daniela.miapp.fragment.CarritoClienteFragment;
import com.daniela.miapp.fragment.EmployeesFragment;
import com.daniela.miapp.fragment.HomeFragment;
import com.daniela.miapp.fragment.InicioClienteFragment;
import com.daniela.miapp.fragment.InicioEmpleadoFragment;
import com.daniela.miapp.fragment.MisPedidosClienteFragment;
import com.daniela.miapp.fragment.OrdersFragment;
import com.daniela.miapp.fragment.PedidosEmpleadoFragment;
import com.daniela.miapp.fragment.PerfilClienteFragment;
import com.daniela.miapp.fragment.ProductsFragment;
import com.daniela.miapp.fragment.ProfileFragment;
import com.daniela.miapp.fragment.TiendaClienteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PrincipalClienteActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipalcliente);

        tvAppName = findViewById(R.id.tvAppName);

        // 🔥 Obtener UID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 🔎 Buscar su nombre desde Firestore
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            tvAppName.setText("Hola " + nombre);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvAppName.setText("Hola cliente");
                    });
        }

        initView();
        initValues();
        initListener();
    }

    // Inicializa las vistas
    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_cliente);
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

                if (itemId == R.id.nav_inicio) {
                    selectedFragment = InicioClienteFragment.newInstance();
                } else if (itemId == R.id.nav_tienda) {
                    selectedFragment = TiendaClienteFragment.newInstance();
                } else if (itemId == R.id.nav_carrito) {
                    selectedFragment = CarritoClienteFragment.newInstance();
                } else if (itemId == R.id.nav_pedidos) {
                    selectedFragment = MisPedidosClienteFragment.newInstance();
                } else if (itemId == R.id.nav_perfil) {
                    selectedFragment = PerfilClienteFragment.newInstance();
                }

                // Cargar el fragmento correspondiente
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameContainerCliente, selectedFragment)
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
                .replace(R.id.frameContainerCliente, fragment)
                .commit();
    }
}
