package com.example.to_dolist;

public class Usuario {
    private String uid;
    private String nombre;
    private String email;
    private String rol; // "admin" o "empleado"

    public Usuario() {} // requerido por Firestore para el mapeo POJO

    public Usuario(String uid, String nombre, String email, String rol) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}