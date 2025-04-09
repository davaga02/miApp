package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;

public class PerfilClienteFragment extends Fragment {

    public PerfilClienteFragment() {
        // Constructor vacío
    }
    public static Fragment newInstance() {
        Bundle args = new Bundle();
        PerfilClienteFragment fragment = new PerfilClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_cliente, container, false);
    }
}
