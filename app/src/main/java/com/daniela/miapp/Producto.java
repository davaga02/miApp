package com.daniela.miapp;

import java.util.Map;

public class Producto {

    private String id;
    private String nombre;
    private String categoria;
    private String subcategoria;
    private String descripcion;
    private int stock;
    private Double precio; // Usado si NO tiene tamaños
    private Map<String, Double> tamanos; // Usado si SÍ tiene tamaños


    public Producto() {
    }

    // Constructor para producto SIN tamaños
    public Producto(String id, String nombre, String categoria, String subcategoria,
                    String descripcion, int stock, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio = precio;
    }


    // Constructor para producto CON tamaños
    public Producto(String id, String nombre, String categoria, String subcategoria,
                    String descripcion, int stock, Map<String, Double> tamanos) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.stock = stock;
        this.tamanos = tamanos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Map<String, Double> getTamanos() {
        return tamanos;
    }

    public void setTamanos(Map<String, Double> tamanos) {
        this.tamanos = tamanos;
    }
}

