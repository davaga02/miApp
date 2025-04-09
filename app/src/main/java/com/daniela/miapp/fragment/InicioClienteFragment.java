package com.daniela.miapp.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;

public class InicioClienteFragment extends Fragment {

    public InicioClienteFragment() {
        // Constructor vacío
    }
    public static InicioClienteFragment newInstance() {

        Bundle args = new Bundle();

        InicioClienteFragment fragment = new InicioClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio_cliente, container, false);
    }
}
