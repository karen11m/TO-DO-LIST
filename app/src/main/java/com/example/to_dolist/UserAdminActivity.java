package com.example.to_dolist;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;



public class UserAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private List<Usuario> usuarios = new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private FirebaseFirestore firestore;
    private android.widget.LinearLayout layoutSinUsuarios;
    private ImageButton btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        layoutSinUsuarios = findViewById(R.id.layoutSinUsuarios);
        btnAtras = findViewById(R.id.btnAtras);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuarioAdapter(usuarios, this);
        recyclerView.setAdapter(adapter);

        btnAtras.setOnClickListener(v -> finish());

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        listenerRegistration = firestore.collection("usuarios")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (value == null) return;

                    usuarios.clear();
                    value.getDocuments().forEach(doc -> {
                        Usuario u = doc.toObject(Usuario.class);
                        if (u != null) {
                            u.setUid(doc.getId());
                            usuarios.add(u);
                        }
                    });
                    adapter.notifyDataSetChanged();
                    boolean vacio = usuarios.isEmpty();
                    recyclerView.setVisibility(vacio ? View.GONE : View.VISIBLE);
                    layoutSinUsuarios.setVisibility(vacio ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}