package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilEmail, tilPassword, tilConfirmar;
    private TextInputEditText etNombre, etEmail, etPassword, etConfirmar;
    private Spinner spinnerRol;
    private MaterialButton btnRegistrar;

    private AuthHelper authHelper;
    private boolean cargando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authHelper = new AuthHelper();

        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        ArrayAdapter<CharSequence> rolesAdapter = ArrayAdapter.createFromResource(
                this, R.array.roles_array, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolesAdapter);

        btnRegistrar.setOnClickListener(v -> intentarRegistro());
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmar = etConfirmar.getText() != null ? etConfirmar.getText().toString().trim() : "";

        limpiarErrores();
        if (!validarCampos(nombre, email, password, confirmar) || cargando) return;

        String rol = spinnerRol.getSelectedItem().toString().equals("Administrador") ? "admin" : "empleado";
        setCargando(true);

        authHelper.registrar(nombre, email, password, rol, new AuthHelper.OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                setCargando(false);
                authHelper.guardarSesionLocal(RegisterActivity.this, rol, nombre);

                // Enviar correo de verificación
                user.sendEmailVerification()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(RegisterActivity.this,
                                        "Cuenta creada. Revisa tu correo para verificarla ✉️",
                                        Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(RegisterActivity.this,
                                        "Cuenta creada correctamente",
                                        Toast.LENGTH_SHORT).show());

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                setCargando(false);
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validarCampos(String nombre, String email, String password, String confirmar) {
        boolean valido = true;
        if (TextUtils.isEmpty(nombre) || nombre.length() < 3) {
            tilNombre.setError("Ingresa un nombre válido (mín. 3 caracteres)");
            valido = false;
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("El correo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            valido = false;
        }
        if (!confirmar.equals(password)) {
            tilConfirmar.setError("Las contraseñas no coinciden");
            valido = false;
        }
        return valido;
    }

    private void limpiarErrores() {
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);
    }

    private void setCargando(boolean cargando) {
        this.cargando = cargando;
        btnRegistrar.setEnabled(!cargando);
        btnRegistrar.setText(cargando ? "Creando cuenta..." : "Registrarse");
    }
}