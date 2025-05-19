package com.daniela.miapp;

public class ProductoSeleccionado {
    private String productoId;
    private int cantidad;
    private String tamaño;
    private String sabor;

    public ProductoSeleccionado(String productoId, int cantidad, String tamaño, String sabor) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.tamaño = tamaño;
        this.sabor = sabor;
    }

    public ProductoSeleccionado(String productoId) {
        this.productoId = productoId;
    }

    // Getters y setters
    public String getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public String getTamaño() { return tamaño; }
    public String getSabor() { return sabor; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setTamaño(String tamaño) { this.tamaño = tamaño; }
    public void setSabor(String sabor) { this.sabor = sabor; }
}
