package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;

public class MisPedidosClienteFragment extends Fragment {

    public MisPedidosClienteFragment() {
        // Constructor vacío
    }
    public static Fragment newInstance() {
        Bundle args = new Bundle();
        MisPedidosClienteFragment fragment = new MisPedidosClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false);
    }
}
