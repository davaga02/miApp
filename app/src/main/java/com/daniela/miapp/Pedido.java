package com.daniela.miapp;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pedido implements Parcelable {
    private String id;
    private String usuario;
    private String mesa;
    private List<Map<String, Object>> productos; // ID del producto y cantidad
    private String estado;
    private long timestamp;

    public Pedido() {
        // Requerido por Firestore
    }


    public Pedido(String id, String usuario, String mesa, List<Map<String, Object>> productos, String estado, long timestamp) {
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

        productos = new ArrayList<>();
        in.readList(productos, Map.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(usuario);
        dest.writeString(mesa);
        dest.writeString(estado);
        dest.writeLong(timestamp);

        dest.writeList(productos);
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


    public List<Map<String, Object>> getProductos() {
        return productos;
    }

    public void setProductos(List<Map<String, Object>> productos) {
        this.productos = productos;
    }
}
