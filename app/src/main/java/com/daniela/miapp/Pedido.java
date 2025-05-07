package com.daniela.miapp;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Pedido implements Parcelable {
    private String id;
    private String usuario;
    private String mesa;
    private Map<String, Integer> productos; // ID del producto y cantidad
    private String estado;
    private long timestamp;

    public Pedido() {
        // Requerido por Firestore
    }

    public Pedido(String id, String usuario, String mesa, Map<String, Integer> productos, String estado, long timestamp) {
        this.id = id;
        this.usuario = usuario;
        this.mesa = mesa;
        this.productos = productos;
        this.estado = estado;
        this.timestamp = timestamp;
    }

    // Getters y setters

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUsuario() { return usuario; }

    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getMesa() { return mesa; }

    public void setMesa(String mesa) { this.mesa = mesa; }

    public Map<String, Integer> getProductos() { return productos; }

    public void setProductos(Map<String, Integer> productos) { this.productos = productos; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Parcelable

    protected Pedido(Parcel in) {
        id = in.readString();
        usuario = in.readString();
        mesa = in.readString();
        estado = in.readString();
        timestamp = in.readLong();

        productos = new HashMap<>();
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                productos.put(key, bundle.getInt(key));
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(usuario);
        dest.writeString(mesa);
        dest.writeString(estado);
        dest.writeLong(timestamp);

        Bundle bundle = new Bundle();
        if (productos != null) {
            for (Map.Entry<String, Integer> entry : productos.entrySet()) {
                bundle.putInt(entry.getKey(), entry.getValue());
            }
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pedido> CREATOR = new Creator<Pedido>() {
        @Override
        public Pedido createFromParcel(Parcel in) {
            return new Pedido(in);
        }

        @Override
        public Pedido[] newArray(int size) {
            return new Pedido[size];
        }
    };


}
