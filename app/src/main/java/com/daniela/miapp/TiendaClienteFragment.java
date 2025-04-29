package com.daniela.miapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TiendaClienteFragment extends Fragment {
    public TiendaClienteFragment() {
        // Constructor vacío
    }
    public static Fragment newInstance() {
        Bundle args = new Bundle();

        TiendaClienteFragment fragment = new TiendaClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tienda_cliente, container, false);
    }
}
