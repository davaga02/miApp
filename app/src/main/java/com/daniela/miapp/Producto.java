
package com.daniela.miapp;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Producto implements Parcelable {

    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int stock;
    private Map<String, Double> precios;
    private String imagenURL;
    private boolean esCategoria;
    private boolean requiereSabor;       // true si necesita elegir un sabor
    private List<String> sabores;        // Lista de sabores disponibles

    public Producto() {}

    public Producto(String id, String nombre, String descripcion, String categoria, int stock, Map<String, Double> precios, String imagenURL, boolean esCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.precios = precios;
        this.imagenURL = imagenURL;
        this.esCategoria = esCategoria;
    }
    public Producto(String nombre, String categoria, Map<String, Double> precios, String imagenURL, boolean esCategoria) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precios = precios;
        this.imagenURL = imagenURL;
        this.esCategoria = esCategoria;
    }

    protected Producto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        descripcion = in.readString();
        categoria = in.readString();
        stock = in.readInt();
        imagenURL = in.readString();
        esCategoria = in.readByte() != 0;

        Bundle bundle = in.readBundle(getClass().getClassLoader());
        if (bundle != null) {
            precios = new HashMap<>();
            for (String key : bundle.keySet()) {
                precios.put(key, bundle.getDouble(key));
            }
        }
    }

    public Producto(String id, String nombre, String descripcion, String categoria, int stock, Map<String, Double> precios, String imagenURL, boolean esCategoria, boolean requiereSabor, List<String> sabores) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.precios = precios;
        this.imagenURL = imagenURL;
        this.esCategoria = esCategoria;
        this.requiereSabor = requiereSabor;
        this.sabores = sabores;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeString(categoria);
        dest.writeInt(stock);
        dest.writeString(imagenURL);
        dest.writeByte((byte) (esCategoria ? 1 : 0));

        Bundle bundle = new Bundle();
        if (precios != null) {
            for (Map.Entry<String, Double> entry : precios.entrySet()) {
                bundle.putDouble(entry.getKey(), entry.getValue());
            }
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    // Getters y Setters (los mismos que ya tienes)

    public boolean esCategoria() { return esCategoria; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }

    public Map<String, Double> getPrecios() { return precios; }

    public void setPrecios(Map<String, Double> precios) { this.precios = precios; }

    public String getImagenURL() { return imagenURL; }

    public void setImagenURL(String imagenURL) { this.imagenURL = imagenURL; }

    public boolean isRequiereSabor() { return requiereSabor; }
    public List<String> getSabores() { return sabores; }

    public boolean isEsCategoria() {
        return esCategoria;
    }

    public void setEsCategoria(boolean esCategoria) {
        this.esCategoria = esCategoria;
    }

    public void setRequiereSabor(boolean requiereSabor) {
        this.requiereSabor = requiereSabor;
    }

    public void setSabores(List<String> sabores) {
        this.sabores = sabores;
    }
}
