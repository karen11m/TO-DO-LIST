package com.example.to_dolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthHelper {
    private static final String TAG = "AuthHelper";
    private static final String USERS_COLLECTION = "usuarios";
    public static final String PREFS_NAME = "TodoListPrefs";
    public static final String KEY_ROL = "rol_usuario";
    public static final String KEY_NOMBRE = "nombre_usuario";

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public interface OnAuthListener {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    public interface OnRolListener {
        void onRolObtenido(String rol);
        void onError(String error);
    }

    public AuthHelper() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registrar(String nombre, String email, String password, String rol, OnAuthListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        listener.onError("No se pudo crear el usuario");
                        return;
                    }
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", nombre);
                    datos.put("email", email);
                    datos.put("rol", rol);

                    firestore.collection(USERS_COLLECTION).document(user.getUid())
                            .set(datos)
                            .addOnSuccessListener(aVoid -> listener.onSuccess(user))
                            .addOnFailureListener(e -> listener.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al registrar", e);
                    listener.onError(e.getMessage());
                });
    }

    public void login(String email, String password, OnAuthListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> listener.onSuccess(authResult.getUser()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al iniciar sesión", e);
                    listener.onError(e.getMessage());
                });
    }

    public void obtenerRolUsuario(String uid, OnRolListener listener) {
        firestore.collection(USERS_COLLECTION).document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        listener.onRolObtenido(rol != null ? rol : "empleado");
                    } else {
                        listener.onError("Usuario no encontrado en la base de datos");
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Cachea el rol localmente para no consultar Firestore en cada pantalla
    public void guardarSesionLocal(Context context, String rol, String nombre) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ROL, rol).putString(KEY_NOMBRE, nombre).apply();
    }

    public String obtenerRolLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROL, "empleado");
    }

    public String obtenerNombreLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NOMBRE, "Usuario");
    }

    // Destruye el caché local Y el token de Firebase (requisito del Momento 3)
    public void cerrarSesion(Context context) {
        auth.signOut();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}