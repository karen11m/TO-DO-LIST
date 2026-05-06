package com.example.to_dolist;

public class Task {
    private int id;
    private String titulo;
    private String descripcion;
    private int estado;
    private String fecha;  // ← NUEVO

    // Constructor con fecha
    public Task(int id, String titulo, String descripcion, int estado, String fecha) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
    }

    // Constructor sin fecha
    public Task(int id, String titulo, String descripcion, int estado) {
        this(id, titulo, descripcion, estado, "");
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getEstado() { return estado; }
    public String getFecha() { return fecha; }

    public void setEstado(int estado) { this.estado = estado; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}