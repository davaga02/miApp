package com.daniela.miapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.daniela.miapp.R;

public class ProductsFragment extends Fragment {

    public  ProductsFragment() {
        // Constructor vacío
    }

    public static ProductsFragment newInstance() {

        Bundle args = new Bundle();

        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

}
