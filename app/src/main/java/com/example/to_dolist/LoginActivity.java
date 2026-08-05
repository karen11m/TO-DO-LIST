package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvIrRegistro;

    private AuthHelper authHelper;
    private boolean cargando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authHelper = new AuthHelper();

        // AUTO-LOGIN: si ya hay sesión activa en Firebase, saltamos directo a MainActivity
        if (authHelper.getCurrentUser() != null) {
            irAMain();
            return;
        }

        setContentView(R.layout.activity_login);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnLogin.setOnClickListener(v -> intentarLogin());
        tvIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void intentarLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (!validarCampos(email, password) || cargando) return;

        setCargando(true);
        authHelper.login(email, password, new AuthHelper.OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                obtenerRolYContinuar(user);
            }

            @Override
            public void onError(String error) {
                setCargando(false);
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void obtenerRolYContinuar(FirebaseUser user) {
        authHelper.obtenerRolUsuario(user.getUid(), new AuthHelper.OnRolListener() {
            @Override
            public void onRolObtenido(String rol) {
                authHelper.guardarSesionLocal(LoginActivity.this, rol, user.getEmail());
                setCargando(false);
                irAMain();
            }

            @Override
            public void onError(String error) {
                setCargando(false);
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validarCampos(String email, String password) {
        boolean valido = true;
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("El correo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("La contraseña es obligatoria");
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            valido = false;
        }
        return valido;
    }

    private void setCargando(boolean cargando) {
        this.cargando = cargando;
        btnLogin.setEnabled(!cargando);
        btnLogin.setText(cargando ? "Ingresando..." : "Iniciar Sesión");
    }

    private void irAMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}