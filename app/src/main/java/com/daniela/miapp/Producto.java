package com.daniela.miapp;

import java.util.Map;

public class Producto {

    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int stock;
    private Map<String, Double> precios;
    private String imagenURL;// Usado si SÍ tiene tamaños
    private boolean esCategoria;


    public Producto() {
    }


    public Producto(String id, String nombre, String descripcion, String categoria, int stock, Map<String, Double> precios, String imagenURL) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.precios = precios;
        this.imagenURL = imagenURL;
    }
    public Producto(String nombre, String categoria, Map<String, Double> precios, String imagenURL, boolean esCategoria) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precios = precios;
        this.imagenURL = imagenURL;
        this.esCategoria = esCategoria;
    }


    public Producto(String id, String nombre, String descripcion, String categoria, int stock, Map<String, Double> precios, String imagenURL,  boolean esCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.precios = precios;
        this.imagenURL = imagenURL;
        this.esCategoria = esCategoria;
    }

    public boolean esCategoria() { return esCategoria; }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Map<String, Double> getPrecios() {
        return precios;
    }


    public void setPrecios(Map<String, Double> precios) {
        this.precios = precios;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}

