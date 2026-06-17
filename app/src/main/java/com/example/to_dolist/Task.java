package com.example.to_dolist;

import java.util.Date;

public class Task {
    private String id;
    private String titulo;
    private String descripcion;
    private boolean completado;
    private Date fecha;
    private String prioridad;   // ← NUEVO
    private String categoria;   // ← NUEVO

    public Task() {}

    public Task(String titulo, String descripcion, boolean completado, Date fecha, String prioridad, String categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completado = completado;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.categoria = categoria;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}