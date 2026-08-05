package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fab;
    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authHelper = new AuthHelper();

        if (authHelper.getCurrentUser() == null) {
            irALogin();
            return;
        }

        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        fab = findViewById(R.id.fab_agregar);

        fab.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditTaskActivity.class)));

        if (savedInstanceState == null) {
            mostrarFragmento(new HomeFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fab.setVisibility(View.VISIBLE);
                mostrarFragmento(new HomeFragment());
                return true;
            } else if (id == R.id.nav_calendar) {
                fab.setVisibility(View.GONE);
                mostrarFragmento(new CalendarFragment());
                return true;
            } else if (id == R.id.nav_stats) {
                fab.setVisibility(View.GONE);
                mostrarFragmento(new StatsFragment());
                return true;
            } else if (id == R.id.nav_settings) {
                fab.setVisibility(View.GONE);
                mostrarFragmento(new SettingsFragment());
                return true;
            }
            return false;
        });
    }

    private void mostrarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void irALogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}